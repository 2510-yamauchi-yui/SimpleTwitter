package chapter6.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chapter6.beans.User;

//settingとeditに対してログインフィルター
@WebFilter(urlPatterns = {"/setting", "/edit"})
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		//ServletRequestをHttpServletRequestに型変換する
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
    	User user = (User) httpRequest.getSession().getAttribute("loginUser");
		
		if (user != null) {
			chain.doFilter(request, response); // サーブレットを実行
		} else {
			//userがnullだったらエラーメッセージをリストに
			List<String> errorMessages = new ArrayList<String>();
			errorMessages.add("ログインしてください");
			
			httpRequest.getSession().setAttribute("errorMessages", errorMessages);
    		//リダイレクト
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
		}
	}	

	@Override
	public void init(FilterConfig config) {
	}

	@Override
	public void destroy() {
	}
}
