package com.diver.diver;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carlos on 29/01/2016.
 */
public class ClubLab {
    private static List<Club> Clubs = new ArrayList<Club>();

    public ClubLab() {
    }

    public static void setClubs(List<Club> clubs) {
        Clubs=clubs;
    }

    public static Club getClubById(int id) {

        for (Club c : Clubs) {
            if (c.getClubID() == id) {
                return c;
            }
        }
        return null;
    }

    public static void deleteClubs(){
        Clubs = new ArrayList<Club>();
    }
    public static void updateClubs(Context context) {

    }

    public static void addClub(Club club){
        Clubs.add(club);
    }

    public static List<Club> getClubs() {
        return Clubs;
    }
}

