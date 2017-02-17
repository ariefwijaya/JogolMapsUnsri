package com.jogoler.jogolmapsunsri.fragment;


import android.support.v4.app.Fragment;

    public class BaseFragment extends Fragment {
    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    public boolean onBackPressed() {
        return false;
    }
}