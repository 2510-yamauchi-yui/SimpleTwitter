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

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

@WebServlet(urlPatterns = { "/signup" })
public class SignUpServlet extends HttpServlet {


   /**
   * ロガーインスタンスの生成
   */
	Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
	public SignUpServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	//get呼び出しされると実行され、signup.jspを表示する
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() + 
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	//request.getRequestDispatcherメソッドの引数に遷移する画面を指定
    	//forwardメソッドを呼び出すと遷移
    	request.getRequestDispatcher("signup.jsp").forward(request, response);
	}

    //post呼び出しされると実行。リクエストパラメータをUserオブジェクトにセットし、
    //Serviceのメソッドを呼び出してDBへユーザーの登録。
    //ユーザーの登録が完了したら再びトップ画面を表示する
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {


    	log.info(new Object(){}.getClass().getEnclosingClass().getName() + 
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	List<String> errorMessages = new ArrayList<String>();

    	//getUserメソッドの呼び出し
    	User user = getUser(request);
    	if (!isValid(user, errorMessages)) {
    		request.setAttribute("errorMessages", errorMessages);
    		request.getRequestDispatcher("signup.jsp").forward(request, response);
    		return;
    	}
    	
    	//入力値に不備がなければUserServiceのinsertメソッドに処理が移る
    	new UserService().insert(user);
    	//URLの指定でリダイレクト
    	response.sendRedirect("./");
	}

    //ユーザー登録画面(signup.jsp)からの入力値（リクエストパラメータ）を取得
	private User getUser(HttpServletRequest request) throws IOException, ServletException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() + 
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());
	
		User user = new User();
		user.setName(request.getParameter("name"));
		user.setAccount(request.getParameter("account"));
		user.setPassword(request.getParameter("password"));
		user.setEmail(request.getParameter("email"));
		user.setDescription(request.getParameter("description"));
		return user;
	}

	//入力値に対するバリデーション。入力値が不正な場合には再度、signupを表示する
	private boolean isValid(User user, List<String> errorMessages) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() + 
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		String name = user.getName();
		String account = user.getAccount();
		String password = user.getPassword();
		String email = user.getEmail();

		if (!StringUtils.isEmpty(name) && (20 < name.length())) {
			errorMessages.add("名前は20文字以下で入力してください");
		}

		if (StringUtils.isEmpty(account)) {
			errorMessages.add("アカウント名を入力してください");
		} else if (20 < account.length()) {
			errorMessages.add("アカウント名は20文字以下で入力してください");
		}

		if (StringUtils.isEmpty(password)) {
			errorMessages.add("パスワードを入力してください");
		}

		if (!StringUtils.isEmpty(email) && (50 < email.length())) {
			errorMessages.add("メールアドレスは50文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
			return true;
		}
}