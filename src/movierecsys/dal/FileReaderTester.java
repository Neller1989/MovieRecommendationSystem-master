/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import java.io.IOException;
import java.util.List;
import movierecsys.be.Movie;

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
        MovieDAO movieDao = new MovieDAO();
//        movieDao.createMovie(2222, "Test Movie");
//        movieDao.createMovie(2223, "Virker vores");
//        movieDao.createMovie(2224, "getNextAvailableId method?");
//        movieDao.deleteMovie(new Movie(17764,1998,"Shakespeare in Love"));
//        movieDao.createMovie(1998, "Shakespeare in Love");
//        movieDao.deleteMovie(new Movie(17769,2003,"The Company"));
//        movieDao.createMovie(2003, "The Company");
//        movieDao.sortMovies();
//        movieDao.updateMovie(new Movie(17769, 2003, "The Company"));
//        movieDao.updateMovie(new Movie(17771, 2003, "The Company: HELLO"));
//        movieDao.deleteMovie(new Movie(17771, 2003, "The Company: HELLO"));
        List<Movie> allMovs = movieDao.getAllMovies();

        for (Movie allMov : allMovs)
        {
            System.out.println(allMov.getTitle());
        }
        System.out.println("Movie count: " + allMovs.size());
        System.out.println("next id " +movieDao.getNextAvailableId());
        System.out.println("The movie you requested is: " + movieDao.movieToString(movieDao.getMovie(17764)));
        

//        System.out.println("Movie count: " + allMovs.size());
    }
    
   
}
