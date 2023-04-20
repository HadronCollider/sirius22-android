package com.example.siriusproject.boofcv;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.siriusproject.R;
import com.example.siriusproject.app.SiriusProjectApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boofcv.io.calibration.CalibrationIO;
import boofcv.struct.calib.CameraPinholeBrown;

public class DemoMain extends AppCompatActivity implements ExpandableListView.OnChildClickListener {

    public static final String TAG = "DemoMain";

    List<Group> groups = new ArrayList<>();

    boolean waitingCameraPermissions = true;

    SiriusProjectApp app;

    String projectPath;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boofsfm);

        app = (SiriusProjectApp) getApplication();
        if (app == null)
            throw new RuntimeException("App is null!");
        projectPath = getIntent().getStringExtra("project_path");

        try {
            loadCameraSpecs();
        } catch (NoClassDefFoundError e) {
            // Some people like trying to run this app on really old versions of android and
            // seem to enjoy crashing and reporting the errors.
            e.printStackTrace();
            abortDialog("Camera2 API Required");
            return;
        }
        createGroups();

        ExpandableListView listView = findViewById(R.id.DemoListView);

        SimpleExpandableListAdapter expListAdapter =
                new SimpleExpandableListAdapter(
                        this,
                        createGroupList(),              // Creating group List.
                        R.layout.group_row,             // Group item layout XML.
                        new String[]{"Group Item"},  // the key of group item.
                        new int[]{R.id.row_name},    // ID of each group item.-Data under the key goes into this TextView.
                        createChildList(),              // childData describes second-level entries.
                        R.layout.child_row,             // Layout for sub-level entries(second level).
                        new String[]{"Sub Item"},      // Keys in childData maps to display.
                        new int[]{R.id.grp_child}     // Data under the keys above go into these TextViews.
                );

        listView.setAdapter(expListAdapter);
        listView.setOnChildClickListener(this);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!waitingCameraPermissions && app.changedPreferences) {
            loadIntrinsics(this, app.preference.cameraId, app.preference.calibration, null);
        }
    }

    public void pressedWebsite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://boofcv.org"));
        startActivity(browserIntent);
    }

    private void createGroups() {
        Group sfm = new Group("Structure From Motion");

        RunBeforeStartActivityFactory runBeforeStartActivityFactory = new RunBeforeStartActivityFactory();
        runBeforeStartActivityFactory.setAction(intent -> intent.putExtra("project_path", projectPath));
        sfm.addChild("Uncalibrated MVS", MultiViewStereoActivity.class, runBeforeStartActivityFactory);

        groups.add(sfm);
    }

    private void loadCameraSpecs() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    0);
        } else {
            waitingCameraPermissions = false;

            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (manager == null)
                throw new RuntimeException("No cameras?!");
            try {
                String[] cameras = manager.getCameraIdList();

                for (String cameraId : cameras) {
                    CameraSpecs c = new CameraSpecs();
                    app.specs.add(c);
                    c.deviceId = cameraId;
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    c.facingBack = facing != null && facing == CameraCharacteristics.LENS_FACING_BACK;
                    StreamConfigurationMap map = characteristics.
                            get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (map == null) {
                        continue;
                    }
                    Size[] sizes = map.getOutputSizes(ImageFormat.YUV_420_888);
                    if (sizes == null)
                        continue;
                    c.sizes.addAll(Arrays.asList(sizes));
                }
            } catch (CameraAccessException e) {
                throw new RuntimeException("No camera access??? Wasn't it just granted?");
            }

            // Now that it can read the camera set the default settings
            setDefaultPreferences();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadCameraSpecs();
                    setDefaultPreferences();
                } else {
                    dialogNoCameraPermission();
                }
                return;
            }
        }
    }

    private void setDefaultPreferences() {
        app.preference.showSpeed = false;
        app.preference.autoReduce = true;

        // There are no cameras.  This is possible due to the hardware camera setting being set to false
        // which was a work around a bad design decision where front facing cameras wouldn't be accepted as hardware
        // which is an issue on tablets with only front facing cameras
        if (app.specs.size() == 0) {
            dialogNoCamera();
        }
        // select a front facing camera as the default
        for (int i = 0; i < app.specs.size(); i++) {
            CameraSpecs c = app.specs.get(i);

            app.preference.cameraId = c.deviceId;
            if (c.facingBack) {
                break;
            }
        }

        if (!app.specs.isEmpty()) {
            loadIntrinsics(this, app.preference.cameraId, app.preference.calibration, null);
        }
    }

    private void dialogNoCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device has no cameras!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dialogNoCameraPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Denied access to the camera! Exiting.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void loadIntrinsics(Activity activity,
                                      String cameraId,
                                      List<CameraPinholeBrown> intrinsics,
                                      List<File> locations) {
        intrinsics.clear();
        if (locations != null)
            locations.clear();

        File directory = new File(getExternalDirectory(activity), "calibration");
        if (!directory.exists())
            return;
        File files[] = directory.listFiles();
        if (files == null)
            return;
        String prefix = "camera" + cameraId;
        for (File f : files) {
            if (!f.getName().startsWith(prefix))
                continue;
            try {
                FileInputStream fos = new FileInputStream(f);
                Reader reader = new InputStreamReader(fos);
                CameraPinholeBrown intrinsic = CalibrationIO.load(reader);
                intrinsics.add(intrinsic);
                if (locations != null) {
                    locations.add(f);
                }
            } catch (RuntimeException | FileNotFoundException ignore) {
            }
        }
    }

    public static File getExternalDirectory(Activity activity) {
        // if possible use a public directory. If that fails use a private one
//		if(Objects.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
//			File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//			if( !dir.exists() )
//				dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//			return new File(dir,"org.boofcv.android");
//		} else {
        return activity.getExternalFilesDir(null);
//		}
    }

    /* Creating the Hashmap for the row */
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> createGroupList() {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (Group g : groups) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("Group Item", g.name);
            result.add(m);
        }

        return result;
    }

    /* creatin the HashMap for the children */
    @SuppressWarnings("unchecked")
    private List<List<Map<String, String>>> createChildList() {

        List<List<Map<String, String>>> result = new ArrayList<List<Map<String, String>>>();
        for (Group g : groups) {
            List<Map<String, String>> secList = new ArrayList<Map<String, String>>();
            for (String c : g.children) {
                Map<String, String> child = new HashMap<String, String>();
                child.put("Sub Item", c);
                secList.add(child);
            }
            result.add(secList);
        }

        return result;
    }

    public void onContentChanged() {
        System.out.println("onContentChanged");
        super.onContentChanged();
    }

    /**
     * Switch to a different activity when the user selects a child from the menu
     */
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        Group g = groups.get(groupPosition);

        Class<Activity> action = g.activities.get(childPosition);
        if (action != null) {
            Intent intent = new Intent(this, action);
            if (g.runBeforeStart.get(childPosition) != null) {
                ((RunBeforeStartActivityFactory) g.runBeforeStart.get(childPosition)).setIntent(intent);
                g.runBeforeStart.get(childPosition).run();
            }
            startActivity(intent);
        }

        return true;
    }


    public static class RunBeforeStartActivityFactory implements Runnable {
        public interface ActionWithIntent {
            void action(Intent intent);
        }
        private Intent intent = null;

        ActionWithIntent action = null;

        public void setIntent(Intent intent) {
            this.intent = intent;
        }
        public void setAction(ActionWithIntent actionWithIntent) {
            this.action = actionWithIntent;
        }

        @Override
        public void run() {
            if (intent != null && action != null)
                Log.e(TAG, "intent == null or action == null");
            action.action(intent);
        }
    }

    private static class Group {
        String name;
        List<String> children = new ArrayList<String>();
        List<Class<Activity>> activities = new ArrayList<Class<Activity>>();
        List<Runnable> runBeforeStart = new ArrayList<>();

        private Group(String name) {
            this.name = name;
        }

        public void addChild(String name, Class activity, Runnable beforeStart) {
            children.add(name);
            activities.add(activity);
            runBeforeStart.add(beforeStart);
        }
    }

    public static CameraSpecs defaultCameraSpecs(SiriusProjectApp app) {
        for (int i = 0; i < app.specs.size(); i++) {
            CameraSpecs s = app.specs.get(i);
            if (s.deviceId.equals(app.preference.cameraId))
                return s;
        }
        throw new RuntimeException("Can't find default camera");
    }

    /**
     * Displays a warning dialog and then exits the activity
     */
    private void abortDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Fatal error");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                    DemoMain.this.finish();
                });
        alertDialog.show();
    }
}
