//import android.os.AsyncTask;
//
//import com.example.gachaurant.JsonParser;
//import com.example.gachaurant.restaurantPackage.Restaurant;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.List;
//
//private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
//    @Override
//    protected List<HashMap<String, String>> doInBackground(String... strings) {
//        //Create json parser class
//        JsonParser jsonParser = new JsonParser();
//        List<HashMap<String, String>> mapList = null;
//        try {
//            JSONObject object = new JSONObject(strings[0]);
//            mapList = jsonParser.parseResult(object);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return mapList;
//    }
//
//    @Override
//    protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
//        //clear map
//        hashMaps.clear();
//        for(int i = 0; i < hashMaps.size(); i++){
//            HashMap<String, String> hashMapList = hashMaps.get(i);
//            double lat = Double.parseDouble(hashMapList.get("lat"));
//            double lng = Double.parseDouble(hashMapList.get("lng"));
//            double distance = calculateDistance(latitude,longitude,lat,lng);
//            if (distance <= radius){
//                double rating = Double.parseDouble(hashMapList.get("rating"));
//                Restaurant restaurant = new Restaurant(hashMapList.get("name"), rating,hashMapList.get("type"), lat, lng);
//                restaurantList.add(restaurant);
//            }
//        }
//    }
//}