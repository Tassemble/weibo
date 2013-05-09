package com.techq.weibo.domain;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WeiboConfig {
	private static final String SEPERATOR = ":";
	private static final Logger LOG = Logger.getLogger(WeiboConfig.class);
	
	String followFanConfig;
	
	String groupConfig;
	
	boolean isGroupSentSwitchOpen = false;
	
	
	Boolean checkCodeSwitchOpen = false;

	
	
	public Boolean getCheckCodeSwitchOpen() {
		return checkCodeSwitchOpen;
	}


	public void setCheckCodeSwitchOpen(Boolean checkCodeSwitchOpen) {
		this.checkCodeSwitchOpen = checkCodeSwitchOpen;
	}
	boolean isFollowFanSwitchOpen = false;//switch
	long followFanInterval = 300L;//seconds
	
	public String getFollowFanConfig() {
		return followFanConfig;
	}
	
	
	
	public boolean isGroupSentSwitchOpen() {
		return isGroupSentSwitchOpen;
	}



	public void setGroupSentSwitchOpen(boolean isGroupSentSwitchOpen) {
		this.isGroupSentSwitchOpen = isGroupSentSwitchOpen;
	}



	public void setGroupConfig(String groupConfig) {
		if (StringUtils.isBlank(groupConfig)) {
			return;
		}
		
		this.isGroupSentSwitchOpen = Boolean.valueOf(groupConfig);
		this.groupConfig = groupConfig;
	}



	public void setFollowFanConfig(String followFanConfig) {
		
		if (StringUtils.isBlank(followFanConfig) || followFanConfig.split(SEPERATOR).length != 2) {
			LOG.warn("you have not set follow fan config or config is not correctly!!!:" + followFanConfig);
			return;
		}
		
		this.followFanConfig = followFanConfig;
		
		List<String> values = Arrays.asList(followFanConfig.split(SEPERATOR));
		try {
			isFollowFanSwitchOpen = Boolean.valueOf(values.get(0));
			followFanInterval = Long.valueOf(values.get(1));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return;
		}
	}
	
	
	
	public boolean isFollowFanSwitchOpen() {
		return isFollowFanSwitchOpen;
	}
	public void setFollowFanSwitchOpen(boolean isFollowFanSwitchOpen) {
		this.isFollowFanSwitchOpen = isFollowFanSwitchOpen;
	}
	
	public long getFollowFanInterval() {
		return followFanInterval;
	}
	public void setFollowFanInterval(long followFanInterval) {
		this.followFanInterval = followFanInterval;
	}



}
