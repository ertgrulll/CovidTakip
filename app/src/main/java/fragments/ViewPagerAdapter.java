package fragments;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import data.DataManager;
import data.DataStruct;

//TODO: #performance fragment sayısını kontrol et, sayfa değişimlerinde önceki fragment'ı hafızadan sil
public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_CHART_PAGES = 6;
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private final DataManager dataManager;
    private ArrayList<DataStruct> data = new ArrayList<>();
    private int position;
    private int id = 0;

    public ViewPagerAdapter(Fragment fragment, DataManager dataManager) {
        super(fragment);
        this.dataManager = dataManager;
        dataManager.setGraphDataListener(graphData -> {
            data = graphData;
            createFragment(this.position);
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(id);
    }

    @Override
    public boolean containsItem(long itemId) {
        return super.containsItem(itemId);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (this.fragments.size() > id) {
            return fragments.get(fragments.size() - 1);
        }
        id++;
        this.position = position;

        if (this.data.size() == 0) {
            LocalChartFragment localChartFragment = new LocalChartFragment(position, dataManager.getGraphData());
            fragments.add(localChartFragment);
            return localChartFragment;
        }
        LocalChartFragment localChartFragment = new LocalChartFragment(position, this.data);
        fragments.add(localChartFragment);
        return localChartFragment;
    }

    /*public void removeLastFragment() {
        if (fragments.size() != 0) {
            int last = fragments.size() - 1;
            FragmentManager fragmentManager = fragments.get(last).getFragmentManager();
            assert fragmentManager != null;
            fragmentManager.beginTransaction().remove(fragments.get(last));
            fragments.remove(last);
        }
        Log.v("chart fragment size", fragments.size() + "");
    }*/

    @Override
    public int getItemCount() {
        return NUM_CHART_PAGES;
    }

    public static class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.75f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            }
            else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                }
                else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            }
            else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }


}

