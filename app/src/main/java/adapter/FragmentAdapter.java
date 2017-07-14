package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import fragment.ChatFragment;
import fragment.Contact;
import fragment.Personcenter;

/**
 * Created by DELL on 2017/7/12.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    //private List<Fragment> FragmentList;
    private Fragment chat_fragment, person_fragment, contact_fragment;
    private static final int PAGE_SIZE = 3;
    private static final int PENSON_FRAGMENT = 2;
    private static final int CHAT_FRAGMENT = 0;
    private static final int CONTACT_FRAGMENT = 1;
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case CHAT_FRAGMENT:
                return chat_fragment;
            case CONTACT_FRAGMENT:
                return contact_fragment;
            case PENSON_FRAGMENT:
                return person_fragment;
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_SIZE;
    }

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        chat_fragment = new ChatFragment();
        contact_fragment = new Contact();
        person_fragment = new Personcenter();
    }

}
