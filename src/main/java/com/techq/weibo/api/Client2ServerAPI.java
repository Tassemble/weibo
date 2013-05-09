package com.techq.weibo.api;

import java.util.List;

import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Task;
import com.techq.weibo.meta.User;


/**
 * 
 * @author chq
 *
 */
public interface Client2ServerAPI extends ClientRobot {

	public Task submitTask2Server(Task task);
	
	/**
	 *  get task from others
	 * @param userId
	 * @return
	 * @throws WeiboException 
	 */
	public List<Task> askForTasks(String userId) throws WeiboException;
	
	
	public void completeTask(Task task) throws WeiboException;
	
	
	/**
	 * 获取一些用户id，这些id还没有粉我过
	 * @param userId
	 * @return
	 * @throws WeiboException 
	 */
	public List<Task> getIDs2FollowMe(String userId, int pageOffset)  throws WeiboException;
	
	/**
	 *  增加一个已经follow的人到toId上
	 * @param fromId
	 * @param toId
	 * @throws WeiboException 
	 */
	public void addFollow(String fromId, String toId) throws WeiboException;
	
	public void heartBeat(String myId) throws WeiboException;
	
	
	public void addUser2Server(User user) throws WeiboException;
	
	
	public User getUser(String userId) throws WeiboException;
	
	
}
