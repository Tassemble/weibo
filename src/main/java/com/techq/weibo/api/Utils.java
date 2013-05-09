package com.techq.weibo.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.apache.http.client.HttpClient;

import com.techq.weibo.dao.DAOLocker;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Response;
import com.techq.weibo.meta.Task;
import com.techq.weibo.share.ProducerComsumer;

/**
 * 
 * @author chq
 *
 */
public interface Utils {

	public String httpGet(HttpClient client, String url) throws WeiboException;
	
	public ScheduledFuture<?> trackTask(Task task, final Task trackedTask) ;
	
	public Response string2Response(String str) throws WeiboException;
	
	public String response2String(Response res);
	
	public  List<Task> string2Tasks(String str);
	
	public String tasks2String(List<Task> tasks);
	
	public ScheduledFuture<?> submitTask(Task task);
	
	public void addFollowTask(Task task);
	
	public void addProduceComsumer(ProducerComsumer<Task> pc);
	
	public DAOLocker getLock();
	
	public void startClientHeartBeat(final Client2ServerAPI api, final String userId);
	
	
	public String map2String(Map<String, Long> map);
	
	public Map<String, Long> string2Map(String str);
	
	public boolean isValid(long fromId, long toId);
	
	public void shutdownService();
	
	public boolean isShutDown();
	
}
