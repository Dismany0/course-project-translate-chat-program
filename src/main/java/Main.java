import gateways.*;
import message_edit_delete_use_case.*;
import message_search_use_case.MessageSearchGateway;
import message_search_use_case.MessageSearchInputBoundary;
import message_search_use_case.MessageSearchInteractor;
import message_search_use_case.MessageSearchOutputBoundary;
import org.jetbrains.annotations.NotNull;
import profile_customization_use_case.CustomizationGateway;
import profile_customization_use_case.CustomizationInputBoundary;
import profile_customization_use_case.CustomizationInteractor;
import profile_customization_use_case.CustomizationOutputBoundary;
import services.DBInitializer;
import services.DBService;
import user_login_use_case.LoginInputBoundary;
import user_login_use_case.UserLoginGateway;
import user_login_use_case.UserLoginInteractor;
import user_register_use_case.UserRegistrationGateway;
import user_register_use_case.UserRegistrationInteractor;
import user_send_message.MessageInputBoundary;
import user_send_message.MessageInteractor;
import user_send_message.SendMessageGateway;
import views.*;
import user_register_use_case.UserRegisterInputBoundary;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Set up Database
        try {
            new DBInitializer().init();
        } catch (Exception ignored) {
        }

        CardLayout cardLayout = new CardLayout();
        JPanel screens = new JPanel(cardLayout);
        JFrame application = new JFrame("Translation App");
        application.add(screens);

        Navigator nav = new CardLayoutNavigator(cardLayout, screens);
        DBService dbs = new DBService();

        // Initialize screens
        JPanel registerScreen = initRegisterScreen(nav, dbs);
        JPanel loginSreen = initLoginScreen(nav, dbs);
        JPanel homeScreen = initHomeScreen(nav, dbs);
        // Add screens to the card layout
        screens.add(registerScreen, "register");
        screens.putClientProperty("register", registerScreen);
        screens.add(loginSreen, "login");
        screens.putClientProperty("login", loginSreen);
        screens.add(homeScreen, "home");
        screens.putClientProperty("home", homeScreen);

        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.setSize(640, 640);
        application.setVisible(true);
        nav.showScreen("register");
    }
    @NotNull
    private static JPanel initHomeScreen(Navigator nav, DBService dbs) {
        Map<String, Object> controllers = new HashMap<>();

        CustomizationGateway customizationGateway = new CustomizationGatewayImplementation();
        CustomizationOutputBoundary customizationPresenter = new CustomizationPresenter();
        CustomizationInputBoundary customizationInteractor = new CustomizationInteractor(customizationGateway,
                customizationPresenter);
        CustomizationController customizationController = new CustomizationController(customizationInteractor);

        MessageSearchGateway messageSearchGateway = new MessageSearchFirebaseSystem();
        MessageSearchOutputBoundary messageSearchPresenter = new MessageSearchPresenter();
        MessageSearchInputBoundary messageSearchInteractor = new MessageSearchInteractor(messageSearchGateway,
                messageSearchPresenter);
        MessageSearchController messageSearchController = new MessageSearchController(messageSearchInteractor);

        MessageEditGateway messageEditGateway = new MessageEditFirebaseSystem();
        MessageEditOutputBoundary messageEditPresenter = new MessageEditPresenter();
        MessageEditInputBoundary messageEditInteractor = new MessageEditInteractor(messageEditGateway,
                messageEditPresenter);
        MessageEditController messageEditController = new MessageEditController(messageEditInteractor);

        MessageDeleteGateway messageDeleteGateway = new MessageDeleteFirebaseSystem();
        MessageDeleteOutputBoundary messageDeletePresenter = new MessageDeletePresenter();
        MessageDeleteInputBoundary messageDeleteInteractor = new MessageDeleteInteractor(messageDeleteGateway,
                messageDeletePresenter);
        MessageDeleteController messageDeleteController = new MessageDeleteController(messageDeleteInteractor);

        SendMessageGateway sendMessageGateway = new SendMessageGatewayImplementation();
        MessageInputBoundary sendMessageInteractor = new MessageInteractor(sendMessageGateway);
        SendMessageController sendMessageController = new SendMessageController(sendMessageInteractor);

        controllers.put("customization", customizationController);
        controllers.put("message_search", messageSearchController);
        controllers.put("message_edit", messageEditController);
        controllers.put("message_delete", messageDeleteController);
        controllers.put("send", sendMessageController);

        return new HomeScreen(getLangs(), controllers, nav);
    }

    @NotNull
    private static JPanel initRegisterScreen(Navigator nav, DBService db) {
        UserRegistrationGateway userFactory = new UserRegistrationFirebaseSystem(db);
        UserRegisterPresenter presenter = new UserRegisterPresenter();
        UserRegisterInputBoundary interactor = new UserRegistrationInteractor(userFactory, presenter);
        UserRegisterController userRegisterController = new UserRegisterController(interactor);

        HashMap<String, String> langs = getLangs();

        return new RegisterScreen(langs, userRegisterController, nav);
    }

    private static HashMap<String, String> getLangs() {
        HashMap<String, String> langs = new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader("src/main/java/languages.txt"))) {
            String line = br.readLine();

            while (line != null) {
                String[] langCode = line.split(" ");
                langCode[0] = langCode[0].strip();
                langCode[1] = langCode[1].strip();
                langs.put(langCode[0], langCode[1]);
                line = br.readLine();
            }
            return langs;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @NotNull
    private static JPanel initLoginScreen(Navigator nav, DBService db) {
        UserLoginGateway auth = new UserLoginFirebaseSystem(db);
        LoginPresenter presenter = new LoginPresenter();
        LoginInputBoundary interactor = new UserLoginInteractor(auth, presenter);
        LoginController userLoginController = new LoginController(interactor);

        return new LoginScreen(userLoginController, nav);
    }

    @NotNull
    private static JPanel initChatScreen(Navigator nav, DBService db) {
        return new JPanel();
    }

}
