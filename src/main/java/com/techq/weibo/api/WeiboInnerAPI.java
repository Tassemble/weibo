package com.techq.weibo.api;

import com.techq.weibo.meta.User;

public interface WeiboInnerAPI extends ClientRobot {

	//<div class="MIB_btn2 lf" id="atnRelation">
	//if not 已关注 ok follow
	
	
	/**
	 * get index html through id
	 */
	public String getIndexHTML(long id);
	
	/**
	 * 
	 * @param currentId
	 * @param followId
	 * @return
	 */
	public boolean hasFollowed(long currentId, long followId);
	
	
	/**
	 * 
	 * @param currentId
	 * @param followId
	 * @return
	 */
	public abstract boolean follow(long currentId, long followId);
	
	
	public User getUser(String userId);
	
	public User getCurrentUser();
	
	
	public boolean hasSuchFan(long myId, long fanId);
	
}
