/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import java.io.IOException;
import java.util.List;
import movierecsys.be.Movie;
import movierecsys.be.Rating;
import movierecsys.be.User;

/**
 *
 * @author pgn
 */
public class FileReaderTester
{

    /**
     * Example method. This is the code I used to create the users.txt files.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        RatingDAO ratingDAO = new RatingDAO();
        UserDAO userDAO = new UserDAO();
        MovieDAO movieDao = new MovieDAO();
//        ratingDAO.createRating(new Rating(17769, 123456, 5));
//        ratingDAO.getAllRatings();
//        
//        ratingDAO.updateRating(new Rating(8,1744889,-3));
//        List<Rating> testrating = ratingDAO.getRatings(new User(1744889,"test"));
//        for (Rating rating : testrating) {
//            System.out.println(rating.toString());
//        }
//        System.out.println(ratingDAO.getRatings(new User(123456,"test")));
//       List<User> allUse = userDAO.getAllUsers();
//       userDAO.createUser("TestUser");
//        userDAO.updateUser(new User(6, "TestUser: Now Updated"));
//        userDAO.deleteUser(new User(6,"TestUser: Now Updated"));
//        
//        for (User user : allUse) {
//            System.out.println(user.toString());
//        }
//        System.out.println("User count: " + allUse.size());
//        System.out.println("next user id " +userDAO.getNextAvailableUserId());
//       System.out.println("Specific user request: "+userDAO.getUser(2649120));
//        List<Movie> allMovs = movieDao.getAllMovies();
//
//        for (Movie allMov : allMovs)
//        {
//            System.out.println(allMov.getTitle());
//        }
//        System.out.println("Movie count: " + allMovs.size());
//        System.out.println("next id " +movieDao.getNextAvailableId());
//        System.out.println("The movie you requested is: " + movieDao.movieToString(movieDao.getMovie(17764)));
//        
    }
    
   
}
