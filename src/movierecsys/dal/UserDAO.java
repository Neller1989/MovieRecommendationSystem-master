/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static javafx.css.StyleOrigin.USER;
import movierecsys.be.Movie;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class UserDAO
{
    private static final String USER_SOURCE = "data/users.txt";
    
    /**
     * Gets a list of all known users.
     * @return List of users.
     */
    public List<User> getAllUsers() throws FileNotFoundException, IOException
    {
        List<User> listOfUsers = new ArrayList<>();
        File file = new File(USER_SOURCE);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                try
                {
                    User use = stringArrayToUser(line);
                    listOfUsers.add(use);
                } catch (Exception ex)
                {
                    //Do nothing we simply do not accept malformed lines of data.
                    //In a perfect world you should at least log the incident.
                }
            }
        }
        return listOfUsers;
    }
    
    /**
     * Reads a user from a , s
     *
     * @param t
     * @return
     * @throws NumberFormatException
     */
    private User stringArrayToUser(String t)
    {
        String[] arrUser = t.split(",");

        int id = Integer.parseInt(arrUser[0]);
        String name = arrUser[1];

        User use = new User(id,name);
        return use;
    }
    
    /**
     * Gets a single User by its ID.
     * @param id The ID of the user.
     * @return The User with the ID.
     */
    public User getUser(int id) throws IOException
    {
        List<User> getUsers = getAllUsers();
        User getAUser = null;
        for (User user : getUsers)
        {
            if (user.getId() == id)
            {
                getAUser = new User(user.getId(), user.getName());
            }
        }
        return getAUser;
    }
    
    /**
     * Updates a user so the persistence storage reflects the given User object.
     * @param user The updated user.
     */
    public void updateUser(User user) throws IOException
    {
        File tmp = new File("data/tmp_userslist.txt");
        List<User> allUsers = getAllUsers();
        if (user.getId() > allUsers.size())
        {
            System.out.println("The User ID does not exist, make sure you are trying to update the right movie");
            System.exit(0);
        }
        allUsers.removeIf((User t) -> t.getId() == user.getId());
        allUsers.add(user);
        Collections.sort(allUsers, (User o1, User o2) -> Integer.compare(o1.getId(), o2.getId()));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp)))
        {
            for (User use : allUsers)
            {
                bw.write(use.getId() + "," + use.getName());
                bw.newLine();
            }
        }
        Files.copy(tmp.toPath(), new File(USER_SOURCE).toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.delete(tmp.toPath());
    }
    
     /**
     * Creates a movie in the persistence storage.
     *
     * @param releaseYear The release year of the movie
     * @param title The title of the movie
     * @return The object representation of the movie added to the persistence
     * storage.
     * @throws java.io.IOException
     */
    public User createUser(String name) throws IOException
    {
        Path path = new File(USER_SOURCE).toPath();

        int id = -1;
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.SYNC, StandardOpenOption.APPEND, StandardOpenOption.WRITE))
        {
            id = getNextAvailableUserId();
            bw.newLine();
            bw.write(id + "," + name);
        }
        sortUsers();
        return new User(id, name);
    }

    /**
     * Gets the lowest available id from the movie source file.
     *
     * @return lowest available id
     * @throws java.io.IOException
     */
    public int getNextAvailableUserId() throws IOException
    {
        int availableId = 1;
        int lastCounted = 0;

        List<User> allUsersNextId = getAllUsers();

        for (int i = 0; i < allUsersNextId.size(); i++)
        {
            if (availableId == allUsersNextId.get(i).getId())
            {
                availableId = 1;
            }
            if (availableId != 1 && allUsersNextId.get(i).getId() == allUsersNextId.size() - 1)
            {
                return availableId;
            } else if (availableId < allUsersNextId.get(i).getId() && allUsersNextId.get(i).getId() - 1 != lastCounted)
            {
                availableId = allUsersNextId.get(i).getId() - 1;
                return availableId;
            } else if (availableId == 1 && allUsersNextId.get(i).getId() == allUsersNextId.size())
            {
                availableId = allUsersNextId.size() + 1;
                return availableId;
            }
            lastCounted = allUsersNextId.get(i).getId();
        }
        return availableId;
    }

    /**
     * Deletes a user from the persistence storage.
     *
     * @param movie The user to delete.
     * @throws java.io.IOException
     */
    public void deleteUser(User user) throws IOException
    {
        File inputFile = new File(USER_SOURCE);
        File tempFile = new File("data/temp_titles.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        User dUser = user;
        String dName = dUser.getName();
        String dId = Integer.toString(dUser.getId());

        String lineToRemove = "" + dId + "," + dName;
        String currentLine;

        while ((currentLine = reader.readLine()) != null)
        {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            if (trimmedLine.equals(lineToRemove))
            {
                continue;
            }
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();

        inputFile.delete();
        tempFile.renameTo(inputFile);
        sortUsers();
    }
    
     /**
     * Sorts the users.txt file so that the user id's are always in an
     * ascending order.
     *
     * @throws IOException
     */
    public void sortUsers() throws IOException
    {
        File tmp = new File("data/tmp_usersort.txt");
        List<User> sortUList = getAllUsers();

        Collections.sort(sortUList, Comparator.comparingInt(User::getId));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp)))
        {
            for (User user : sortUList)
            {
                bw.write(user.toString());
                bw.newLine();
            }
        }
        Files.copy(tmp.toPath(), new File(USER_SOURCE).toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.delete(tmp.toPath());
    }
}
