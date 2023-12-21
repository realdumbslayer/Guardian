package com.example.guardian;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.onboarding.PaperOnboardingFragment;
import com.example.onboarding.PaperOnboardingPage;
import com.example.onboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class FragmentsActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragments_activity_layout);
        fragmentManager = getSupportFragmentManager();

        final PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(getDataForOnboarding());

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, onBoardingFragment);
        fragmentTransaction.commit();

        onBoardingFragment.setOnRightOutListener(() -> {
            FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
            Fragment bf = new BlankFragment();
            fragmentTransaction1.replace(R.id.fragment_container, bf);
            fragmentTransaction1.commit();
        });

    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Contacts", "Here you can set up your trusted contacts",
                Color.parseColor("#FFFFFF"), R.drawable.ic_outline_person_24, R.drawable.boardicon);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("NFC", "Enable NFC in your quick panel to enable scanning",
                Color.parseColor("#FFFFFF"), R.drawable.ic_shortcut_nfc, R.drawable.boardicon);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Fake Call", "Here you can fake a call",
                Color.parseColor("#FFFFFF"), R.drawable.ic_outline_call_24, R.drawable.boardicon);
        PaperOnboardingPage scr4 = new PaperOnboardingPage("All Done!", "Now you're ready to use the app!",
                Color.parseColor("#FFFFFF"), R.drawable.boardicon, R.drawable.boardicon);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr4);
        return elements;
    }
}
