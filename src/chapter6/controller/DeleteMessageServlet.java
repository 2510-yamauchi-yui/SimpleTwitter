package chapter6.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/deleteMessage" })
public class DeleteMessageServlet extends HttpServlet {
	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	 */
	public DeleteMessageServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		HttpSession session = request.getSession();
		//messageIdを受け取る
		String messageIdStr = request.getParameter("messageId");
		//文字列としてのmessageidをintに変換
		int messageId = Integer.parseInt(messageIdStr);
		//loginUserを受け取る
		User user = (User) session.getAttribute("loginUser");
		if (user == null) {
			response.sendRedirect("./");
			return;
		}

		//messageIdとuserIdを引数として渡す
		new MessageService().delete(messageId, user.getId());

		response.sendRedirect("./");
	}
}
