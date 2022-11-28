package contact_usecases.add_contact_use_case;


import java.util.concurrent.ExecutionException;

public interface AddContactInputBoundary {
    /**
     * Add a contact
     */

    void addContact(Long userID, Long contactID) throws ExecutionException, InterruptedException;
}
