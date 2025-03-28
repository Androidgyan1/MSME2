package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.UserAdapter;
import com.example.myapplication.Model.UserData;

import java.util.ArrayList;
import java.util.List;

public class RecycleApplicant extends AppCompatActivity {
    TextView ApplicantName,ApplicantNo,Category,Qualification,Gender,Email,MobileNo,Pancard,IndustryActivity,ProjectCost,SchemeCode,RegistrationCode
            ,FinanceId,AccountNoBeneficiary,CapitalExpenditureApproved,CapitalExpenditureFinance;

    String APPLICANTNAME,APPLICANTNO,CATEGORY,QUALIFICATION;

    AppCompatButton buttonProceed;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recycle_applicant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ApplicantName = findViewById(R.id.ApplicantName);
        ApplicantNo = findViewById(R.id.ApplicantNo);
        Category = findViewById(R.id.Category);
        Qualification = findViewById(R.id.Qualification);
        Gender = findViewById(R.id.Gender);
        Email = findViewById(R.id.Email);
        MobileNo = findViewById(R.id.MobileNo);
        Pancard = findViewById(R.id.Pancard);
        IndustryActivity = findViewById(R.id.IndustryActivity);
        ProjectCost = findViewById(R.id.ProjectCost);
        SchemeCode = findViewById(R.id.SchemeCode);
        RegistrationCode = findViewById(R.id.RegistrationCode);
        FinanceId = findViewById(R.id.FinanceId);
        AccountNoBeneficiary = findViewById(R.id.AccountNoBeneficiary);
        CapitalExpenditureApproved = findViewById(R.id.CapitalExpenditureApproved);
        CapitalExpenditureFinance = findViewById(R.id.CapitalExpenditureFinance);
        buttonProceed = findViewById(R.id.buttonProceed);

        /// /button lissener

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecycleApplicant.this,CameraActivity.class);
                startActivity(i);
            }
        });



        // Retrieve data from the Intent
        Intent intent = getIntent();
        APPLICANTNAME = intent.getStringExtra("ApplicantName");
        APPLICANTNO = intent.getStringExtra("APPLICANTNO");
        CATEGORY = intent.getStringExtra("Category");
        QUALIFICATION = intent.getStringExtra("Qualification");
        String GENDER = intent.getStringExtra("Gender");
        String EMAIL = intent.getStringExtra("Email");
        String MOBILENO = intent.getStringExtra("MobileNo");
        String PANCARD = intent.getStringExtra("Pancard");
        String INDUSTRYACTIVITY = intent.getStringExtra("IndustryActivity");
        String PROJECTCOAST = intent.getStringExtra("ProjectCost");
        String SCHEMECODE = intent.getStringExtra("SchemeCode");
        String REGISTRATIONCODE = intent.getStringExtra("RegistrationCode");
        String FININCIALID = intent.getStringExtra("FinanceId");
        String ACCOUNTBANAFICERY = intent.getStringExtra("AccountNoBeneficiary");
        String CAPITAL = intent.getStringExtra("CapitalExpenditureApproved");
        String APPROVED = intent.getStringExtra("CapitalExpenditureFinance");

        ApplicantName.setText(APPLICANTNAME);
        ApplicantNo.setText(APPLICANTNO);
        Category.setText(CATEGORY);
        Qualification.setText(QUALIFICATION);
        Gender.setText(GENDER);
        Email.setText(EMAIL);
        MobileNo.setText(MOBILENO);
        Pancard.setText(PANCARD);
        IndustryActivity.setText(INDUSTRYACTIVITY);
        ProjectCost.setText(PROJECTCOAST);
        SchemeCode.setText(SCHEMECODE);
        RegistrationCode.setText(REGISTRATIONCODE);
        FinanceId.setText(FININCIALID);
        AccountNoBeneficiary.setText(ACCOUNTBANAFICERY);
        CapitalExpenditureApproved.setText(CAPITAL);
        CapitalExpenditureFinance.setText(APPROVED);
    }
}