package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	HttpSession session = request.getSession();
    	List<String> errorMessages = new ArrayList<String>();
    	String messageIdStr = request.getParameter("messageId");

    	//idが空欄、数字以外だったときにエラー
		if (StringUtils.isBlank(messageIdStr) || !messageIdStr.matches("^[0-9]+$")) {
			errorMessages.add("不正なパラメータが入力されました");
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		int messageId = Integer.parseInt(messageIdStr);
		Message message = new MessageService().select(messageId);

		//存在しないidを入力したときにエラー
		if (message == null) {
			errorMessages.add("不正なパラメータが入力されました");
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

	    //取得したmessageをセットする
		request.setAttribute("message", message);
		//setting.jspを表示する
		request.getRequestDispatcher("edit.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	HttpSession session = request.getSession();
    	List<String> errorMessages = new ArrayList<String>();

    	//パラメータ取得
    	String text = request.getParameter("text");
    	String messageIdStr = request.getParameter("messageId");

    	if (!isValid(text, errorMessages)) {
    		session.setAttribute("errorMessages", errorMessages);
    		response.sendRedirect("./");
    		return;
    	}

    	//messageIdをint型に変換
    	int messageId = Integer.parseInt(messageIdStr);

    	//MessageServiceから元のメッセージを取得
    	MessageService messageService = new MessageService();
    	Message message = messageService.select(messageId);
    	//新しいテキストに置き換える
    	message.setText(text);

    	User user = (User) session.getAttribute("loginUser");
    	message.setUserId(user.getId());

    	//DB更新
    	new MessageService().update(message);

    	response.sendRedirect("./");
	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}
