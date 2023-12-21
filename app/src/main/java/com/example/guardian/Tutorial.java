package com.example.guardian;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.onboarding.PaperOnboardingFragment;
import com.example.onboarding.PaperOnboardingPage;

import java.util.ArrayList;

public class Tutorial extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_fragments_activity_layout);
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
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Choose your trusted contacts",
                "Share your live location with your friends or family members so that they can follow your live location whenever you want.",
                Color.parseColor("#FFFFFF"), R.drawable.ic_outline_person_24, R.drawable.boardicon);

        PaperOnboardingPage scr2 = new PaperOnboardingPage("Access to your NFC",
                "Enable NFC in your quick panel to enable scanning",
                Color.parseColor("#FFFFFF"), R.drawable.ic_shortcut_nfc, R.drawable.boardicon);

        PaperOnboardingPage scr3 = new PaperOnboardingPage("Access to your location",
                "Activate the location so that your trusted contacts can view your live location whenever you decide to share it with them",
                Color.parseColor("#FFFFFF"), R.drawable.ic_outline_call_24, R.drawable.boardicon);

        PaperOnboardingPage scr4 = new PaperOnboardingPage("Access to your SMS",
                "Give us permission to send SMS messages to your trusted contacts via app or NFC",
                Color.parseColor("#FFFFFF"), R.drawable.ic_message, R.drawable.boardicon);

        PaperOnboardingPage scr5 = new PaperOnboardingPage("Access to your contacts",
                "Give us permission to access your contacts so you could pick out your emergency contacts",
                Color.parseColor("#FFFFFF"), R.drawable.ic_outline_person_24, R.drawable.boardicon);

        PaperOnboardingPage scr6 = new PaperOnboardingPage("Fake Call",
                "If you feel threatened, you can start fake a call",
                Color.parseColor("#FFFFFF"), R.drawable.ic_outline_call_24, R.drawable.boardicon);

        PaperOnboardingPage scr7 = new PaperOnboardingPage("All Done!",
                "Now you're ready to use the app again!",
                Color.parseColor("#FFFFFF"), R.drawable.boardicon, R.drawable.boardicon);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr4);
        elements.add(scr5);
        elements.add(scr6);
        elements.add(scr7);
        return elements;
    }
}
