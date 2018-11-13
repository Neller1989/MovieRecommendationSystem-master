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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import movierecsys.be.Movie;

/**
 *
 * @author pgn
 */
public class MovieDAO
{

    //tester1234
    private static final String MOVIE_SOURCE = "data/movie_titles.txt";

    /**
     * Gets a list of all movies in the persistence storage.
     *
     * @return List of movies.
     * @throws java.io.FileNotFoundException
     */
    public List<Movie> getAllMovies() throws FileNotFoundException, IOException
    {
        List<Movie> allMovies = new ArrayList<>();
        File file = new File(MOVIE_SOURCE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                try
                {
                    Movie mov = stringArrayToMovie(line);
                    allMovies.add(mov);
                } catch (Exception ex)
                {
                    //Do nothing we simply do not accept malformed lines of data.
                    //In a perfect world you should at least log the incident.
                }
            }
        }
        return allMovies;
    }

    /**
     * Reads a movie from a , s
     *
     * @param t
     * @return
     * @throws NumberFormatException
     */
    private Movie stringArrayToMovie(String t)
    {
        String[] arrMovie = t.split(",");

        int id = Integer.parseInt(arrMovie[0]);
        int year = Integer.parseInt(arrMovie[1]);
        String title = arrMovie[2];

        Movie mov = new Movie(id, year, title);
        return mov;
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
    public Movie createMovie(int releaseYear, String title) throws IOException
    {
        Path path = new File(MOVIE_SOURCE).toPath();

        int id = -1;
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.SYNC, StandardOpenOption.APPEND, StandardOpenOption.WRITE))
        {
            id = getNextAvailableId();
            bw.newLine();
            bw.write(id + "," + releaseYear + "," + title);
        }
        sortMovies();
        return new Movie(id, releaseYear, title);
    }

    /**
     * Gets the lowest available id from the movie source file.
     *
     * @return lowest available id
     * @throws java.io.IOException
     */
    public int getNextAvailableId() throws IOException
    {
        int availableId = 1;
        int lastCounted = 0;

        List<Movie> allMoviesNextId = getAllMovies();

        for (int i = 0; i < allMoviesNextId.size(); i++)
        {
            if (availableId == allMoviesNextId.get(i).getId())
            {
                availableId = 1;
            }
            if (availableId != 1 && allMoviesNextId.get(i).getId() == allMoviesNextId.size() - 1)
            {
                return availableId;
            } else if (availableId < allMoviesNextId.get(i).getId() && allMoviesNextId.get(i).getId() - 1 != lastCounted)
            {
                availableId = allMoviesNextId.get(i).getId() - 1;
                return availableId;
            } else if (availableId == 1 && allMoviesNextId.get(i).getId() == allMoviesNextId.size())
            {
                availableId = allMoviesNextId.size() + 1;
                return availableId;
            }
            lastCounted = allMoviesNextId.get(i).getId();
        }
        return availableId;
    }

    /**
     * Deletes a movie from the persistence storage.
     *
     * @param movie The movie to delete.
     * @throws java.io.IOException
     */
    public void deleteMovie(Movie movie) throws IOException
    {
        File inputFile = new File(MOVIE_SOURCE);
        File tempFile = new File("data/temp_titles.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        Movie dMovie = movie;
        String dTitle = dMovie.getTitle();
        String dRelease = Integer.toString(dMovie.getYear());
        String dId = Integer.toString(dMovie.getId());

        String lineToRemove = "" + dId + "," + dRelease + "," + dTitle;
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
        sortMovies();
    }

    /**
     * Updates the movie in the persistence storage to reflect the values in the
     * given Movie object.
     *
     * @param movie The updated movie.
     */
    public void updateMovie(Movie movie) throws IOException
    {
        File tmp = new File("data/tmp_movies.txt");
        List<Movie> allMovies = getAllMovies();
        if (movie.getId() > allMovies.size())
        {
            System.out.println("The movie ID does not exist, make sure you are trying to update the right movie");
            System.exit(0);
        }
        allMovies.removeIf((Movie t) -> t.getId() == movie.getId());
        allMovies.add(movie);
        Collections.sort(allMovies, (Movie o1, Movie o2) -> Integer.compare(o1.getId(), o2.getId()));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp)))
        {
            for (Movie mov : allMovies)
            {
                bw.write(mov.getId() + "," + mov.getYear() + "," + mov.getTitle());
                bw.newLine();
            }
        }
        Files.copy(tmp.toPath(), new File(MOVIE_SOURCE).toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.delete(tmp.toPath());
    }

    /**
     * Gets a the movie with the given ID.
     *
     * @param id ID of the movie.
     * @return A Movie object.
     */
    public Movie getMovie(int id) throws IOException
    {
        List<Movie> getMovieList = getAllMovies();
        Movie getAMov = null;
        for (Movie movie : getMovieList)
        {
            if (movie.getId() == id)
            {
                getAMov = new Movie(movie.getId(), movie.getYear(), movie.getTitle());
            }
        }
        return getAMov;
    }

    /**
     * Sorts the movie_titles.txt file so that the movie id's are always in an
     * ascending order.
     *
     * @throws IOException
     */
    public void sortMovies() throws IOException
    {
        File tmp = new File("data/tmp.txt");
        List<Movie> sortMList = getAllMovies();

        Collections.sort(sortMList, Comparator.comparingInt(Movie::getId));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmp)))
        {
            for (Movie movie : sortMList)
            {
                bw.write(movieToString(movie));
                bw.newLine();
            }
        }
        Files.copy(tmp.toPath(), new File(MOVIE_SOURCE).toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.delete(tmp.toPath());
    }

    /**
     * Takes a movie object and converts its variables into string
     *
     * @param movie
     * @return a string consisting of a movie object's id,releaseYear,title
     */
    public String movieToString(Movie movie)
    {
        Movie dMovie = movie;
        String dTitle = dMovie.getTitle();
        String dRelease = Integer.toString(dMovie.getYear());
        String dId = Integer.toString(dMovie.getId());

        String movieString = dId + "," + dRelease + "," + dTitle;
        return movieString;
    }

}
