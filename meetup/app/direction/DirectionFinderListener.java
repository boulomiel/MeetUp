package com.rubenmimoun.meetup.app.direction;

import com.rubenmimoun.meetup.app.Models.Route;

import java.util.List;

public interface DirectionFinderListener {
        void onDirectionFinderStart();
        void onDirectionFinderSuccess(List<Route> route);
    }


