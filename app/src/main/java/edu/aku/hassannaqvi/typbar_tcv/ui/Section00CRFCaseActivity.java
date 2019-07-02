package edu.aku.hassannaqvi.typbar_tcv.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.aku.hassannaqvi.typbar_tcv.R;
import edu.aku.hassannaqvi.typbar_tcv.contracts.FormsContract;
import edu.aku.hassannaqvi.typbar_tcv.contracts.HFContract;
import edu.aku.hassannaqvi.typbar_tcv.core.DatabaseHelper;
import edu.aku.hassannaqvi.typbar_tcv.core.MainApp;
import edu.aku.hassannaqvi.typbar_tcv.databinding.ActivitySection00CrfCaseBinding;
import edu.aku.hassannaqvi.typbar_tcv.utils.DateUtils;
import edu.aku.hassannaqvi.typbar_tcv.validation.ClearClass;
import edu.aku.hassannaqvi.typbar_tcv.validation.ValidatorClass;

public class Section00CRFCaseActivity extends AppCompatActivity {

    ActivitySection00CrfCaseBinding bi;
    DatabaseHelper db;
    Map<String, HFContract> hfMap;
    List<String> hfName = new ArrayList<>(Arrays.asList("...."));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_section00_crf_case);
        bi.setCallback(this);
        EventsCall();

        setContentUI();
        loadHFFromDB();

        //setListeners();
    }

    private void loadHFFromDB() {
        Collection<HFContract> allHF = db.getAllHF();
        if (allHF.size() == 0) return;
        hfName = new ArrayList<>();
        hfName.add("....");
        hfMap = new HashMap<>();
        for (HFContract hf : allHF) {
            hfName.add(hf.getHfname());
            hfMap.put(hf.getHfname(), hf);
        }
        filledSpinners(hfName);
    }

    private void filledSpinners(List<String> hfNames) {
    }

    private void setContentUI() {
        this.setTitle(R.string.CrfCase);


        // Initialize db
        db = new DatabaseHelper(this);
    }

    public void BtnContinue() {
        try {

            if (!formValidation()) return;

//            if (!MainApp.checkingGPSRules(this)) return;

            SaveDraft();

            if (!UpdateDB()) {
                Toast.makeText(this, "Error in updating db!!", Toast.LENGTH_SHORT).show();
                return;
            }
             else {
                startActivity(new Intent(this, EndingActivity.class).putExtra("complete", false));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean UpdateDB() {

        DatabaseHelper db = new DatabaseHelper(this);
        long updcount = db.addForm(MainApp.fc);
        MainApp.fc.set_ID(String.valueOf(updcount));
        if (updcount > 0) {
            MainApp.fc.setUID((MainApp.fc.getDeviceID() + MainApp.fc.get_ID()));
            db.updateFormID();

            return true;
        }

        return false;
    }

    private void SaveDraft() throws JSONException {
        MainApp.fc = new FormsContract();
        MainApp.fc.setDevicetagID(getSharedPreferences("tagName", MODE_PRIVATE).getString("tagName", null));
        MainApp.fc.setFormDate(new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date().getTime()));
        MainApp.fc.setUser(MainApp.userName);
        MainApp.fc.setDeviceID(Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        MainApp.fc.setAppversion(MainApp.versionName + "." + MainApp.versionCode);
        settingGPS(MainApp.fc);
        MainApp.fc.setFormtype("sca");

        JSONObject crfCase = new JSONObject();

        //crfCase.put("tcvcl00", bi.tcvcl00.getSelectedItem());

        //child.put("sch_code", schoolMap.get(bi.tcvcl01.getSelectedItem()).getSch_code());
        //child.put("sch_add", schoolMap.get(bi.tcvcl01.getSelectedItem()).getSch_add());
        //child.put("sch_type", schoolMap.get(bi.tcvcl01.getSelectedItem()).getSch_type());
        //child.put("tcvcl01", bi.tcvcl01.getSelectedItem());


        crfCase.put("tcvscaa01", bi.tcvscaa01.getText().toString());
        crfCase.put("tcvscaa02", bi.tcvscaa02.getText().toString());
        crfCase.put("tcvscaa03", bi.tcvscaa03.getText().toString());
        crfCase.put("tcvscaa03a", bi.tcvscaa03a.getText().toString());
        crfCase.put("tcvscaa04", bi.tcvscaa04.getText().toString());

        crfCase.put("tcvscaa05ageDob", bi.tcvscaa05ageDob.getText().toString());
        crfCase.put("tcvscaa05", bi.tcvscaa05aAgea.isChecked() ? "1" : bi.tcvscaa05aAgeb.isChecked() ? "2" : "0");
        crfCase.put("tcvscaa06", bi.tcvscaa06a.isChecked() ? "1" : bi.tcvscaa06b.isChecked() ? "2" : "0");
        crfCase.put("tcvscaa07", bi.tcvscaa07.getText().toString());
        crfCase.put("tcvscaa08", bi.tcvscaa08.getText().toString());

        crfCase.put("tcvscab09", bi.tcvscab09a.isChecked() ? "1" : bi.tcvscab09b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab10", bi.tcvscab10a.isChecked() ? "1" : bi.tcvscab10b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab11", bi.tcvscab11a.isChecked() ? "1" : bi.tcvscab11b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab12", bi.tcvscab12.getText().toString());
        crfCase.put("tcvscab13", bi.tcvscab13a.isChecked() ? "1" : bi.tcvscab13b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab14", bi.tcvscab14.getText().toString());
        crfCase.put("tcvscab15", bi.tcvscab15a.isChecked() ? "1" : bi.tcvscab15b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab16", bi.tcvscab16.getText().toString());
        crfCase.put("tcvscab171", bi.tcvscab171a.isChecked() ? "1" : bi.tcvscab171b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab172", bi.tcvscab172a.isChecked() ? "1" : bi.tcvscab172b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab173", bi.tcvscab173a.isChecked() ? "1" : bi.tcvscab173b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab174", bi.tcvscab174a.isChecked() ? "1" : bi.tcvscab174b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab175", bi.tcvscab175a.isChecked() ? "1" : bi.tcvscab175b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab176", bi.tcvscab176a.isChecked() ? "1" : bi.tcvscab176b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab176x", bi.tcvscab176x.getText().toString());
        crfCase.put("tcvscab18", bi.tcvscab18a.isChecked() ? "1" : bi.tcvscab18b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab19", bi.tcvscab19.getText().toString());
        crfCase.put("tcvscab20", bi.tcvscab20a.isChecked() ? "1" : bi.tcvscab20b.isChecked() ? "2" : bi.tcvscab20c.isChecked() ? "3" : "0");
        crfCase.put("tcvscab211", bi.tcvscab211a.isChecked() ? "1" : bi.tcvscab211b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab212", bi.tcvscab212a.isChecked() ? "1" : bi.tcvscab212b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab213", bi.tcvscab213a.isChecked() ? "1" : bi.tcvscab213b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab214", bi.tcvscab214a.isChecked() ? "1" : bi.tcvscab214b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab215", bi.tcvscab215a.isChecked() ? "1" : bi.tcvscab215b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab216", bi.tcvscab216a.isChecked() ? "1" : bi.tcvscab216b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab216x", bi.tcvscab216x.getText().toString());
        crfCase.put("tcvscab22", bi.tcvscab22a.isChecked() ? "1" : bi.tcvscab22b.isChecked() ? "2" : "0");
        crfCase.put("tcvscab23", bi.tcvscab23.getText().toString());

        MainApp.fc.setsA(String.valueOf(crfCase));

    }

    private boolean formValidation() {
        return ValidatorClass.EmptyCheckingContainer(this, bi.llcrf);
    }

    public void BtnEnd() {
        try {
            if (!ValidatorClass.EmptyCheckingContainer(this, bi.ll0104)) return;

//            if (!MainApp.checkingGPSRules(this)) return;
            SaveDraft();

            if (!UpdateDB()) {
                Toast.makeText(this, "Error in updating db!!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, EndingActivity.class).putExtra("complete", false));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void settingGPS(FormsContract fc) {
        MainApp.LocClass locClass = MainApp.setGPS(this);
        fc.setGpsLat(locClass.getLatitude());
        fc.setGpsLng(locClass.getLongitude());
        fc.setGpsAcc(locClass.getAccuracy());
        fc.setGpsDT(locClass.getTime());
    }

    //ClearAllFields

    void EventsCall() {


    }

}
