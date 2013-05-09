package com.techq.weibo.api.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.techq.weibo.api.UserInfoAPI;

public class UserInfoAPIImp implements UserInfoAPI {

	@Override
	public long getFansNum(String html) {
		String regex = "id=\"attentions\">(\\d+)</a>";

		List<String> list = UserInfoAPIImp.getResult(html, regex);
		if (list.size() > 0)
			return Long.valueOf(list.get(0));

		return 0;
	}

	@Override
	public long getUserAge(String html) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUserArea(String html) {
		String regex = "<p><em>([\u4e00-\u9fa5]*)</em><em>([\u4e00-\u9fa5]*)</em></p></div>";

		List<String> list = UserInfoAPIImp.getResult(html, regex);
		
		String area = "";
		for (int i = 0; i < list.size(); i++) {
			area += list.get(i) + " ";
		}

		return area;
	}

	@Override
	public String getUserId(String html) {
		String regex = "\\$CONFIG\\['uid'\\] = '(\\d+)';";
		System.out.println(html);
		List<String> list = UserInfoAPIImp.getResult(html, regex);
		if (list.size() > 0)
			return list.get(0);

		return "";
	}

	@Override
	public long getUserSex(String html) {
		String regex = "class=\"info_radio01\" checked=\"checked\"><label for=\"woman\">([\u4e00-\u9fa5]*)</label>";
		
		List<String> list = UserInfoAPIImp.getResult(html, regex);
		if (list.size() > 0) {
		//  if find return woman
			return 1;
			
		}

		//return man
		return 0;
	}

	@Override
	public String getUserUrl(String html) {
		String regex = "<li class=\"cur\"><a href=\"(http://weibo\\.com/\\d+)\">";
		
		List<String> list = UserInfoAPIImp.getResult(html, regex);
		if (list.size() > 0)
			return list.get(0);

		return "";
		
	}

	@Override
	public String getUsername(String html) {
		String regex = "scope.nikename = \"([\\w\u4e00-\u9fa5]*)\";";
		
		List<String> list = UserInfoAPIImp.getResult(html, regex);
		if (list.size() > 0)
			return list.get(0);

		return "";
	}

	public static List<String> getResult(String html, String regex) {
		if (html == null)
			return new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(html);

		
		List<String> list = new ArrayList<String>();
		if (matcher.find()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				list.add(matcher.group(i + 1));
			}
		}
		return list;
	}

	public static void main(String[] args) {
//		System.out
//				.println(UserInfoAPIImp
//						.getResult("<p><em>浙江</em><em>杭州</em></p></div> ",
//								"<p><em>([\u4e00-\u9fa5]*)</em><em>([\u4e00-\u9fa5]*)</em></p></div>"));
//		
//		System.out
//		.println(new UserInfoAPIImp().getUsername(" scope.nikename = \"小YY哈\";"));
////	}
		System.out.println(new UserInfoAPIImp().getUserId("$CONFIG['uid'] = '1772403527';"));
	}

}
