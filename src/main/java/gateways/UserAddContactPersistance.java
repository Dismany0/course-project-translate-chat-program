package gateways;

import contact_usecases.add_contact_use_case.UserAddContactGateway;
import entities.Chat;
import entities.User;
import org.checkerframework.checker.units.qual.C;
import services.DBService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserAddContactPersistance implements UserAddContactGateway {
    DBService dbService;

    public UserAddContactPersistance() {
        this.dbService = new DBService();
    }

    @Override
    public void addContact(Integer userID, Integer contactID) throws ExecutionException, InterruptedException {

        User targetUser = dbService.getUserDetails(userID);
        dbService.addContact(targetUser, Long.valueOf(contactID));

        List<Integer> chatIDs = null;
        chatIDs = dbService.getAllIDs("chats");
        int nextChatID = Collections.max(chatIDs) + 1;
        List<User> users = new ArrayList<>();
        users.add(dbService.getUserDetails(userID));
        users.add(dbService.getUserDetails(contactID));
        Chat chatToAdd = new Chat(nextChatID, users);
        dbService.addChat(chatToAdd);
    }

    @Override
    public User getUserDetails(int userID) {
        try {
            return dbService.getUserDetails(userID);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
