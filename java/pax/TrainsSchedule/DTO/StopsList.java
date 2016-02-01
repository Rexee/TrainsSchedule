package pax.TrainsSchedule.DTO;

import java.util.ArrayList;
import java.util.List;

import pax.TrainsSchedule.Model.Stop;
import pax.TrainsSchedule.Util.Util;

public class StopsList {
    public Pagination  pagination;
    public List<Train> threads;
    SearchDetail search;

    public Stop.List getStops() {
        if (threads == null) return null;

        int i = threads.size();
        ArrayList<Stop> res = new ArrayList<Stop>(i);

        int dayOfWeek = Util.getDayOfWeek(search.date);

        for (Train train : threads) {

            Stop stop = new Stop();
            stop.fill(search.from, search.to, dayOfWeek, train.departure, train.arrival, train.duration, train.thread.title, train.stops, train.thread.express_type);

            res.add(stop);
        }

        return new Stop.List(res);
    }

    public boolean hasNextPage() {
        if (pagination == null) return false;
        return pagination.has_next && pagination.page < pagination.page_count;
    }

    public int getNextPage() {
        if (pagination == null) return 1;
        return pagination.page + 1;
    }

    public boolean isStartPage() {
        if (pagination == null) return true;
        return pagination.page == 1;
    }
}
