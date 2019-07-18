package com.example.goforlunch;

import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.goforlunch.model.Api.Details.PlaceDetail;
import com.example.goforlunch.model.Api.Distance.DistanceMatrix;
import com.example.goforlunch.model.Api.Nearby.NearbyPlaces;
import com.example.goforlunch.utils.PlaceStreams;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


@RunWith(AndroidJUnit4ClassRunner.class)
public class StreamsTest {
    private String key;
    private String placeId;
    private String location;
    private int radius;
    private String type;
    private String origin;
    private String destinations;

    @Before
    public void setAttributes(){
      key = BuildConfig.GOOGLE_MAPS_API_KEY;
      placeId = "ChIJN5YFJ19HjUcRWk3bUV_A7gQ";
      location = "46.991984, 5.691809";
      radius = 5000;
      type = "restaurant";
      origin = "46.991984, 5.691809";
      destinations = "46.997490, 5.702183";
    }

    @Test
    public void checkNearbyPlace() {
        Observable<NearbyPlaces> nearbyPlacesObservable = PlaceStreams.streamFetchNearbySearch(location, radius, type, key);
        TestObserver<NearbyPlaces> testObserver = new TestObserver<>();
        nearbyPlacesObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        NearbyPlaces nearbyPlaces = testObserver.values().get(0);
        assertTrue(nearbyPlaces.getResults().size() > 0);

    }

    @Test
    public void checkRestaurantInfo(){
        Observable<PlaceDetail> placeDetailObservable = PlaceStreams.streamFetchPlaceDetails(placeId, key);
        TestObserver<PlaceDetail> testObserver = new TestObserver<>();
        placeDetailObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        PlaceDetail placeDetail = testObserver.values().get(0);
        assertEquals("Le Bouchon des Radeliers", placeDetail.getResult().getName());
    }

    @Test
    public void checkDistance(){

        Observable<DistanceMatrix> distanceMatrixObservable = PlaceStreams.streamfetchDistanceMatrix(origin, destinations, key);
        TestObserver<DistanceMatrix> testObserver = new TestObserver<>();
        distanceMatrixObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();
        DistanceMatrix distanceMatrix = testObserver.values().get(0);
        assertTrue(distanceMatrix.getRows().get(0).getElements().get(0).getDistance().getValue() > 0
                && distanceMatrix.getRows().get(0).getElements().get(0).getDistance().getValue() < 1500);
    }
}
