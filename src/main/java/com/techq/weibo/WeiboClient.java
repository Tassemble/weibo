package com.techq.weibo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

import com.techq.weibo.api.Client2ServerAPI;
import com.techq.weibo.api.WeiboInnerAPI;
import com.techq.weibo.api.imp.Client2ServerAPIImp;
import com.techq.weibo.api.imp.WeiboInnerAPIImp;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Task;
import com.techq.weibo.meta.User;
import com.techq.weibo.share.ClientConfig;
import com.techq.weibo.share.Consumer;
import com.techq.weibo.share.Producer;
import com.techq.weibo.share.Share;

public class WeiboClient {

	// 主要测试这个类
	Logger logger = Logger.getLogger(WeiboClient.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		new WeiboClient().start("hisjhdf@12.com", "test");
	}

	public void start(String email, String password) {
		// if user already stored then read info from server
		// else store user info to server

		try {
			// small than 50 then ask add
			final Client2ServerAPI c2s = new Client2ServerAPIImp();

			final WeiboInnerAPI wb = new WeiboInnerAPIImp();

			// login success
			if (!wb.login(email, password)) {
				logger.warn("login fail!");
			}
			logger.info("login successful");

			final User user = wb.getUser(null);
			user.setEmail(email);
			user.setPassword(password);
			c2s.addUser2Server(user);
			Share.utils.startClientHeartBeat(c2s, user.getUserId());
			// 1772403527
			// 跟踪我的
			final BlockingQueue<Task> myFollowsQueue = new ArrayBlockingQueue<Task>(100);
			final Producer myFollowsProducer = new Producer(myFollowsQueue) {
				@Override
				public void run() {
					// code here
					List<Task> myRequests = new CopyOnWriteArrayList<Task>(user
							.getMyRequests());
					int offset = 0;
					while (true) {
						if (offset == 20)
							offset = 0;
						if (myRequests.size() < ClientConfig.TASKS_LIMIE) {
							List<Task> tasks;
							try {
								logger.info("get ids 2 follow me");
								tasks = c2s.getIDs2FollowMe(user.getUserId(),
									 0);

							} catch (WeiboException e) {
								logger.error("WeiboException", e);
								continue;
							}
							logger.info("list follow me tasks:" + tasks);
							for (Task t : tasks) {
								if (!Share.utils.isValid(t.getFromId(), t.getToId()))
									continue;
								if (wb.hasSuchFan(t.getToId(), t.getFromId()))
									continue;
								myFollowsQueue.add(t);
							}
						}
						try {
							TimeUnit.SECONDS.sleep(ClientConfig.TIME_2_GET_IDS_FOLLOW_ME);
						} catch (InterruptedException e) {
							logger.error(e);
							e.printStackTrace();
						}

					}
				}
			};
			final Consumer myFollowsConsumer = new Consumer(myFollowsQueue) {
				@Override
				public void run() {
					int count = 0;
					while (true) {
						Task t = myFollowsProducer.get();
						logger.info("get ready to submit task:" + t);
						try {
							if (wb.checkIfFollowed(String.valueOf(t.getToId())) == true) {
								logger.info("check is true, disable submit task");
								continue;
							}
						} catch (WeiboException e1) {
							// TODO Auto-generated
							// catch block
							e1.printStackTrace();
						}
						logger.info("submit count:" + count++);
						c2s.submitTask2Server(t);
						try {
							TimeUnit.SECONDS.sleep(ClientConfig.TIME_2_SUBMIT);
						} catch (InterruptedException e) {
							logger.error(e);
							e.printStackTrace();
						}
					}
				}
			};

			final BlockingQueue<Task> otherFollowsQueue = new ArrayBlockingQueue<Task>(100);
			final Producer otherFollowsProducer = new Producer(otherFollowsQueue) {
				@Override
				public void run() {
					// code here
					List<Task> otherTasks = new CopyOnWriteArrayList<Task>(user
							.getOthersTasks());
					while (true) {
						logger.info("get other tasks");
						if (otherTasks.size() < ClientConfig.TASKS_LIMIE) {
							List<Task> tasks;
							try {
								tasks = c2s.askForTasks(user.getUserId());
								//logger.info("result:" + tasks);
							} catch (WeiboException e) {
								logger.error("", e);
								continue;
							}


							if (tasks != null) {
								for (Task t : tasks) {
									if (!Share.utils.isValid(t.getFromId(), t
											.getToId())) {
										continue;
									}
									otherFollowsQueue.add(t);
								}
							}
							
							try {
								TimeUnit.SECONDS.sleep(ClientConfig.TIME_2_ASK_TASK);
							} catch (InterruptedException e) {
								logger.error(e);
								e.printStackTrace();
							}
							
							
						}

					}
				}
			};
			Consumer otherFollowsConsumer = new Consumer(otherFollowsQueue) {
				@Override
				public void run() {
					while (true) {
						Task t = otherFollowsProducer.get();
						try {
							if (!Share.utils.isValid(t.getFromId(), t.getToId())) {
								logger.info("cancel invalid follow:from "
										+ t.getFromId() + " to " + t.getToId());

							} else {
								if (wb.checkIfFollowed(String.valueOf(t.getToId())) == true) {
									c2s.addFollow(String.valueOf(t.getFromId()),
											String.valueOf(t.getToId()));
									TimeUnit.SECONDS
											.sleep(ClientConfig.TIME_2_CHECK_IF_FOLLOWED);
								} else {
									logger.info("follow:from " + t.getFromId()
											+ " to " + t.getToId());
									wb.follow(t.getFromId(), t.getToId());
									TimeUnit.SECONDS.sleep(1);
									if (wb.checkIfFollowed(String.valueOf(t
											.getToId())) == true) {
										logger.info("complete task hahah");
										c2s.completeTask(t);
									}
									TimeUnit.SECONDS
											.sleep(ClientConfig.TIME_2_FOLLOW);
								}
							}
						} catch (WeiboException e) {
							logger.error("code:" + e.getCode() + " " + e.getMessage(), e);
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};

			Share.utils.addProduceComsumer(myFollowsProducer);
			Share.utils.addProduceComsumer(myFollowsConsumer);
			Share.utils.addProduceComsumer(otherFollowsProducer);
			Share.utils.addProduceComsumer(otherFollowsConsumer);

			TimeUnit.SECONDS.sleep(1000);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void start(String userId) {
		// if user already stored then read info from server
		// else store user info to server

		try {
			// small than 50 then ask add
			
			final Client2ServerAPI c2s = new Client2ServerAPIImp();

			final WeiboInnerAPI wb = new WeiboInnerAPIImp() {
				
				List<String> follows = new ArrayList<String>();
				@Override
				public boolean hasSuchFan(long myId, long fanId) {
					return follows.contains(String.valueOf(fanId));
				}
				
				@Override
				public boolean checkIfFollowed(String userId) throws WeiboException {
					return follows.contains(userId);
				}
				
				
				@Override
				public boolean follow(long currentId, long followId) {
					follows.add(String.valueOf(followId));
					return true;
				}
			};


			final User user = new User();
			user.setUserId(userId);
		//	Share.user = user;
			c2s.addUser2Server(user);
			Share.utils.startClientHeartBeat(c2s, userId);
			// 1772403527
			// 跟踪我的
			final BlockingQueue<Task> myFollowsQueue = new ArrayBlockingQueue<Task>(100);
			final Producer myFollowsProducer = new Producer(myFollowsQueue) {
				@Override
				public void run() {
					// code here
					List<Task> myRequests = new CopyOnWriteArrayList<Task>(user
							.getMyRequests());
					int offset = 0;
					while (true) {
						if (offset == 20)
							offset = 0;
						if (myRequests.size() < ClientConfig.TASKS_LIMIE) {
							List<Task> tasks;
							try {
								logger.info("get ids 2 follow me");
								tasks = c2s.getIDs2FollowMe(user.getUserId(),
									 0);

							} catch (WeiboException e) {
								logger.error("WeiboException", e);
								continue;
							}
							logger.info("list follow me tasks:" + tasks);
							for (Task t : tasks) {
								if (!Share.utils.isValid(t.getFromId(), t.getToId()))
									continue;
								if (wb.hasSuchFan(t.getToId(), t.getFromId()))
									continue;
								myFollowsQueue.add(t);
							}
						}
						try {
							TimeUnit.SECONDS.sleep(ClientConfig.TIME_2_GET_IDS_FOLLOW_ME);
						} catch (InterruptedException e) {
							logger.error(e);
							e.printStackTrace();
						}

					}
				}
			};
			final Consumer myFollowsConsumer = new Consumer(myFollowsQueue) {
				@Override
				public void run() {
					int count = 0;
					while (true) {
						Task t = myFollowsProducer.get();
						logger.info("get ready to submit task:" + t);
						try {
							if (wb.checkIfFollowed(String.valueOf(t.getToId())) == true) {
								logger.info("check is true, disable submit task");
								continue;
							}
						} catch (WeiboException e1) {
							// TODO Auto-generated
							// catch block
							e1.printStackTrace();
						}
						logger.info("submit count:" + count++);
						c2s.submitTask2Server(t);
						try {
							TimeUnit.SECONDS.sleep(ClientConfig.TIME_2_SUBMIT);
						} catch (InterruptedException e) {
							logger.error(e);
							e.printStackTrace();
						}
					}
				}
			};

			final BlockingQueue<Task> otherFollowsQueue = new ArrayBlockingQueue<Task>(100);
			final Producer otherFollowsProducer = new Producer(otherFollowsQueue) {
				@Override
				public void run() {
					// code here
					List<Task> otherTasks = new CopyOnWriteArrayList<Task>(user
							.getOthersTasks());
					while (true) {
						logger.info("get other tasks");
						if (otherTasks.size() < ClientConfig.TASKS_LIMIE) {
							List<Task> tasks;
							try {
								tasks = c2s.askForTasks(user.getUserId());
								//logger.info("result:" + tasks);
							} catch (WeiboException e) {
								logger.error("", e);
								continue;
							}


							if (tasks != null) {
								for (Task t : tasks) {
									if (!Share.utils.isValid(t.getFromId(), t
											.getToId())) {
										continue;
									}
									otherFollowsQueue.add(t);
								}
							}
							
							try {
								TimeUnit.SECONDS.sleep(ClientConfig.TIME_2_ASK_TASK);
							} catch (InterruptedException e) {
								logger.error(e);
								e.printStackTrace();
							}
							
							
						}

					}
				}
			};
			Consumer otherFollowsConsumer = new Consumer(otherFollowsQueue) {
				@Override
				public void run() {
					while (true) {
						Task t = otherFollowsProducer.get();
						try {
							if (!Share.utils.isValid(t.getFromId(), t.getToId())) {
								logger.info("cancel invalid follow:from "
										+ t.getFromId() + " to " + t.getToId());

							} else {
								if (wb.checkIfFollowed(String.valueOf(t.getToId())) == true) {
									c2s.addFollow(String.valueOf(t.getFromId()),
											String.valueOf(t.getToId()));
									TimeUnit.SECONDS
											.sleep(ClientConfig.TIME_2_CHECK_IF_FOLLOWED);
								} else {
									logger.info("follow:from " + t.getFromId()
											+ " to " + t.getToId());
									wb.follow(t.getFromId(), t.getToId());
									TimeUnit.SECONDS.sleep(1);
									if (wb.checkIfFollowed(String.valueOf(t
											.getToId())) == true) {
										logger.info("complete task hahah");
										c2s.completeTask(t);
									}
									TimeUnit.SECONDS
											.sleep(ClientConfig.TIME_2_FOLLOW);
								}
							}
						} catch (WeiboException e) {
							logger.error("code:" + e.getCode() + " " + e.getMessage(), e);
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};

			Share.utils.addProduceComsumer(myFollowsProducer);
			Share.utils.addProduceComsumer(myFollowsConsumer);
			Share.utils.addProduceComsumer(otherFollowsProducer);
			Share.utils.addProduceComsumer(otherFollowsConsumer);

			TimeUnit.SECONDS.sleep(1000);
		} catch (Throwable e) {
			logger.error(e);
		}
	}

}
