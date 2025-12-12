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

import chapter6.beans.User;
import chapter6.exception.NoRowsUpdatedRuntimeException;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

@WebServlet(urlPatterns = { "/setting" })
public class SettingServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
	public SettingServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	//セッションを取得
    	HttpSession session = request.getSession();
    	//セッションからloginUser属性を取り出す
    	User loginUser = (User) session.getAttribute("loginUser");

    	//UserServiceのselectメソッドを呼び出す
    	User user = new UserService().select(loginUser.getId());

    	//取得したuserをセットする
    	request.setAttribute("user", user);
    	//setting.jspを表示する
    	request.getRequestDispatcher("setting.jsp").forward(request, response);
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	//セッションを取得
    	HttpSession session = request.getSession();
    	//エラーメッセージのリスト
    	List<String> errorMessages = new ArrayList<String>();

    	User user = getUser(request);
    	if (isValid(user, errorMessages)) {
    		try {
    			//UserServiceのupdateメソッド呼び出し
    			new UserService().update(user);
    		} catch (NoRowsUpdatedRuntimeException e) {
    			log.warning("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
    			errorMessages.add("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
    		}
    	}

    	//エラーがあったときエラーメッセージと入力値をセットしてsetting.jspを表示
    	if (errorMessages.size() != 0) {
    		request.setAttribute("errorMessages", errorMessages);
    		request.setAttribute("user", user);
    		request.getRequestDispatcher("setting.jsp").forward(request, response);
    		return;
    	}

    	session.setAttribute("loginUser", user);
    	response.sendRedirect("./");
	}

    private User getUser(HttpServletRequest request) throws IOException, ServletException {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	User user = new User();
    	//文字列のIdをintに変換してセット
    	user.setId(Integer.parseInt(request.getParameter("id")));
    	user.setName(request.getParameter("name"));
    	user.setAccount(request.getParameter("account"));
    	user.setPassword(request.getParameter("password"));
    	user.setEmail(request.getParameter("email"));
    	user.setDescription(request.getParameter("description"));
    	return user;
    }
	
	private boolean isValid(User user, List<String> errorMessages) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		int id = user.getId();
		String name = user.getName();
		String account = user.getAccount();
		String email = user.getEmail();

		User existingAccount = new UserService().select(account);
		
		//名前が空欄ではなく、20文字を超えていた時にエラー
		if (!StringUtils.isEmpty(name) && (20 < name.length())) {
			errorMessages.add("名前は20文字以下で入力してください");
		}
		
		//アカウント名が空欄だった時にエラー
		if (StringUtils.isEmpty(account)) {
			errorMessages.add("アカウント名を入力してください");
		//アカウント名が20文字を超えていたときにエラー
		} else if (20 < account.length()) {
			errorMessages.add("アカウント名は20文字以下で入力してください");
		}
		
		//アカウントが既に存在していて、更新しようとしているidと一致しない時にエラー
		if ((existingAccount != null) && existingAccount.getId() != id) {
			errorMessages.add("既に存在するアカウントです");
		}
		
		//メールアドレスが空欄ではなく、50文字を超えていたときにエラー
		if (!StringUtils.isEmpty(email) && (50 < email.length())) {
			errorMessages.add("メールアドレスは50文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		
		return true;
	}
}