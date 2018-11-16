package jack.com.jkutilsdemo;

import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import jack.com.jkutils.file.FileUtils;
import jack.com.jkutils.network.BasicNetworking;
import jack.com.jkutils.network.Reachability;
import jack.com.jkutils.view.carousel.CarouselView;
import jack.com.jkutilsdemo.model.Student;
import jack.com.jkutilsdemo.model_0.Teacher;

public class MainActivity extends AppCompatActivity {

    String TAG = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        testNetwork();
//        testFile();

        testCarousel();
    }

    void testKeyValue() {

        Student student = new Student("PiPi", 13, 8);

        Teacher teacher = new Teacher("MaoMao", 30, 8);

        teacher.setValueForKey("age", null);

        teacher.setValueForKey("favoriteStudent", student);

        teacher.setValueForKeyPath("favoriteStudent.name", "DouDou");

        Log.d(TAG, String.format("%s favorite student is %s",teacher.getValueForKeyPath("name"),teacher.getValueForKeyPath("favoriteStudent.name")));

    }

    void testNetwork() {

//        String url = "http://192.168.0.130:8080/MyWeb/Test";
//        BasicNetworking.sharedNetworking().getContentOfURL(url, new BasicNetworking.CompletionHandler() {
//            @Override
//            public void completion(BasicNetworking.Response response, Object responseObject, Error error) {
//
//                Log.d(TAG, "Response Body: " + responseObject);
//                Log.d(TAG, "Response Error: " + error);
//                if (response != null) {
//                    Log.d(TAG, "Response Code: " + response.code);
//                    Log.d(TAG, "Response Header: " + response.header);
//                }
//
//            }
//        });


        String url = "https://www.baidu.com/ ";
        JSONObject json = null;

        String js = "{\n" +
                "  \"name\": \"jdMoLfvj+nII/CDsxWfMSg==\",\n" +
                "  \"password\": \"CzADM7u51eNR7ScfQtJw7w==\",\n" +
                "  \"app_short_ver\": \"1.0\",\n" +
                "  \"platform\": \"android\",\n" +
                "  \"offset\": \"0\",\n" +
                "  \"limit\": \"20\"\n" +
                "}";

        try {
            json = new JSONObject(js);


        } catch (JSONException e) {
            e.printStackTrace();
        }

//        BasicNetworking.sharedNetworking().postFormData(url, json, new BasicNetworking.CompletionHandler() {
//            @Override
//            public void completion(BasicNetworking.Response response, Object responseObject, Error error) {
//
//
//                Log.d(TAG, "Response Body: " + responseObject);
//                Log.d(TAG, "Response Error: " + error);
//                if (response != null) {
//                    Log.d(TAG, "Response Code: " + response.code);
//                    Log.d(TAG, "Response Header: " + response.header);
//                }
//
//            }
//
//            @Override
//            public void progressChange(float progress) {
//                Log.d(TAG, "progressChange: " + progress);
//            }
//
//        });

//        BasicNetworking.sharedNetworking().postMultipartData(url, json, new BasicNetworking.CompletionHandler() {
//            @Override
//            public void completion(BasicNetworking.Response response, Object responseObject, Error error) {
//
//                Log.d(TAG, "Response Body: " + responseObject);
//                Log.d(TAG, "Response Error: " + error);
//                if (response != null) {
//                    Log.d(TAG, "Response Code: " + response.code);
//                    Log.d(TAG, "Response Header: " + response.header);
//                }
//            }
//
//            @Override
//            public void progressChange(float progress) {
//                Log.d(TAG, "progressChange: " + progress);
//            }
//        });

//        String dir = FileUtils.sharedUtils().deviceProtectedStoragePath(this);
//
//        String downloadUrl = "http://m6.pc6.com/xuh6/Sketch512.zip";
//        BasicNetworking.sharedNetworking().downloadFile(downloadUrl, dir, new BasicNetworking.CompletionHandler() {
//            @Override
//            public void completion(BasicNetworking.Response response, Object responseObject, Error error) {
//
//                Log.d(TAG, "Response Body: " + responseObject);
//                Log.d(TAG, "Response Error: " + error);
//                if (response != null) {
//                    Log.d(TAG, "Response Code: " + response.code);
//                    Log.d(TAG, "Response Header: " + response.header);
//                }
//            }
//
//            @Override
//            public void progressChange(float progress) {
//
//                Log.d(TAG, "progressChange: " + progress);
//            }
//        });


//        String uploadUrl = "http://192.168.0.130:8080/MyWeb/upload.do";
//        String filePath = FileUtils.sharedUtils().sandboxCacheDirectory(this) + File.separator + "712ff939-97b4-482f-911f-619e68e2ea83";
//        File file = new File(filePath);
//
//        BasicNetworking.sharedNetworking().uploadFile(file, json, uploadUrl, new BasicNetworking.CompletionHandler() {
//            @Override
//            public void progressChange(float progress) {
//
//                Log.d(TAG, "progressChange: " + progress);
//            }
//
//            @Override
//            public void completion(BasicNetworking.Response response, Object responseObject, Error error) {
//
//                Log.d(TAG, "Response Body: " + responseObject);
//                Log.d(TAG, "Response Error: " + error);
//                if (response != null) {
//                    Log.d(TAG, "Response Code: " + response.code);
//                    Log.d(TAG, "Response Header: " + response.header);
//                }
//            }
//        });

//        Reachability.sharedReachability(this).startMonitor(new Reachability.NetworkStateChangeCallback() {
//            @Override
//            public void networkStateChanged(Reachability.NetworkStatus status, NetworkInfo networkInfo) {
//
//                Log.d(TAG, "networkStateChanged: " + status);
//            }
//        });
    }

    void testFile() {

        String dir = FileUtils.sharedUtils().sandboxCacheDirectory(this);
        File f = FileUtils.sharedUtils().createFileInDir("test",dir);
        if (f == null) {

            Log.d(TAG, "Create File: failed");

        } else {

            Log.d(TAG, "Create File: " + f.getAbsolutePath());
        }

    }

    void testCarousel() {

        CarouselView c = findViewById(R.id.carousel);

        final ArrayList<String> a = new ArrayList();
        a.add("1");
        a.add("2");
        a.add("3");
        a.add("4");
        a.add("5");
        a.add("6");

        c.setAutoScroll(true);
        c.registResourceId(R.layout.carousel_cell);
        c.setDelegate(new CarouselView.CarouselDelegate() {

            @Override
            public void carouselWillShowItem(CarouselView carousel, View cell, int index) {
//                cell.setBackgroundColor(getColor());
                String s = a.get(index);
                TextView tv = cell.findViewById(R.id.tv_number);
                tv.setText(s);
            }

            @Override
            public void carouselDidShowItem(CarouselView carousel, int index) {
//                Log.d("Scroll", "carouselDidShowItem: " + index);
            }

            @Override
            public int carouselNumberOfItems(CarouselView carousel) {
                return a.size();
            }
        });
        c.reloadData();

    }
}
