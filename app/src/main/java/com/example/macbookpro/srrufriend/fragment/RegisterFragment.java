package com.example.macbookpro.srrufriend.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.macbookpro.srrufriend.MainActivity;
import com.example.macbookpro.srrufriend.R;
import com.example.macbookpro.srrufriend.utility.AddUser;
import com.example.macbookpro.srrufriend.utility.MasterAlert;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class RegisterFragment extends Fragment {
    private Uri uri;
    private ImageView imageView;
    private boolean photoABoolean = true;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Create Toolbar
        createToolbar();

        //call click on avarta
        avatarController();
    }

    private void createToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.toolbar_register);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);

        //setup Title
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Register");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Please Fill Every Blank");

        //Setup Navigation Icon
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        setHasOptionsMenu(true);

    }


    private void avatarController() {
        imageView = getView().findViewById(R.id.img_avata);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Please choose Image my App"), 1);
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {

            photoABoolean = false;

            uri = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 800, 600, true);
                imageView.setImageBitmap(bitmap1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadToServer() {

        MasterAlert masterAlert = new MasterAlert(getActivity());

        EditText etUser = getView().findViewById(R.id.et_user);
        EditText etEmail = getView().findViewById(R.id.et_email);
        EditText etPass = getView().findViewById(R.id.et_password);

        String stUser = etUser.getText().toString().trim();
        String stEmail = etEmail.getText().toString().trim();
        String stPass = etPass.getText().toString().trim();

        //Check choose photo
        if (photoABoolean) {
            masterAlert.normalDialog("Choose Avata", "Please Choose Avata");
        } else if (stUser.isEmpty() || stEmail.isEmpty() || stPass.isEmpty()) {
            masterAlert.normalDialog("Have Space", "Please Fill Every Blank");
        } else {
            //upload image avata
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            String[] strings = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver()
                    .query(uri, strings, null, null, null);

            String pathAvataString = null;

            if (cursor != null) {

                cursor.moveToFirst();
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                pathAvataString = cursor.getString(index);

            } else {

                pathAvataString = uri.getPath();

            }


            Log.d("29JuneV1", "Path ==> " + pathAvataString);

            File file = new File(pathAvataString);
            FTPClient ftpClient = new FTPClient();


            try {
                ftpClient.connect("ftp.androidthai.in.th", 21);
                ftpClient.login("srru@androidthai.in.th", "Abc12345");
                ftpClient.setType(FTPClient.TYPE_BINARY);
                ftpClient.changeDirectory("Avata");
                ftpClient.upload(file, new MyUploadAvata());


            } catch (Exception e) {
                e.printStackTrace();

                try {
                    ftpClient.disconnect(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            } // try1

            String nameAvata = pathAvataString.substring(pathAvataString.lastIndexOf("/"));
            nameAvata = "http://androidthai.in.th/srru/" + nameAvata;
            try {
                AddUser addUser = new AddUser(getActivity());
                addUser.execute(stUser, stEmail, stPass, nameAvata, "http://androidthai.in.th/srru/addData.php");

                if(Boolean.parseBoolean(addUser.get())){
                    Toast.makeText(getActivity(), "Register Success", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }else {
                    Toast.makeText(getActivity(), "Cannot Upload", Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }//upload to server
    }




    public class MyUploadAvata implements FTPDataTransferListener {

        @Override
        public void started() {
            Toast.makeText(getActivity(), "Alert upload Avata", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void transferred(int i) {
            Toast.makeText(getActivity(), "Continue upload Avata", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void completed() {
            Toast.makeText(getActivity(), "Success upload Avata", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void aborted() {

        }

        @Override
        public void failed() {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_register, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemUpload) {
            uploadToServer();
        }
        return super.onOptionsItemSelected(item);
    }
}
