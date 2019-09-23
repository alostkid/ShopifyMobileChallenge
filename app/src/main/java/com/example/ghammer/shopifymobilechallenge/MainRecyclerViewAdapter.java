package com.example.ghammer.shopifymobilechallenge;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.os.Handler;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private List<String> imageUrls;
    private int numCards;
    private int numMatch;
    private int pickCounter = 0;
    private int score = 0;
    private Set<Integer> pickedIndexSet = new HashSet<>();
    private Context context;
    private RecyclerView recyclerView;


    MainRecyclerViewAdapter(Context context, int numCards, List<String> imageUrls, int matchCount) {
        this.context = context;
        this.numCards = numCards;
        this.imageUrls = imageUrls;
        this.numMatch = matchCount;
    }
    @NonNull
    @Override
    public MainRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainRecyclerViewAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.imageSwitcher.setImageResource(R.drawable.ic_launcher_background);
        viewHolder.imageSwitcher.setVisibility(View.VISIBLE);
        viewHolder.imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUrl = imageUrls.get(i);
                pickCounter++;
                pickedIndexSet.add(i);
                viewHolder.imageSwitcher.setClickable(false);
                Glide.with(context).load(currentUrl).transition(DrawableTransitionOptions.withCrossFade(100)).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (pickCounter == numMatch) {
                                    boolean allMatch = true;
                                    String randomUrl = imageUrls.get(pickedIndexSet.iterator().next());
                                    for (Integer j : pickedIndexSet) {
                                        String matchUrl = imageUrls.get(j);
                                        if (!matchUrl.equals(randomUrl)) {
                                            allMatch = false;
                                            break;
                                        }
                                    }
                                    if (allMatch) {
                                        Animation slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
                                        for(Integer k:pickedIndexSet) {
                                            ViewHolder currVH = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(k);
                                            currVH.imageSwitcher.startAnimation(slideOut);
                                            currVH.imageSwitcher.setVisibility(View.INVISIBLE);
                                        }
                                        score++;
                                        if (score == numCards) {
                                            Toast.makeText(context, "WOW CONGRATS YOU DID IT.", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        for(Integer k:pickedIndexSet) {
                                            ViewHolder currVH = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(k);
                                            currVH.imageSwitcher.setImageResource(R.drawable.ic_launcher_background);
                                            currVH.imageSwitcher.setClickable(true);
                                        }
                                    }
                                    pickedIndexSet.clear();
                                    pickCounter = 0;
                                }
                            }
                        }, 100);
                        return false;
                    }
                }).into((ImageView)viewHolder.imageSwitcher.getCurrentView());
            }
        });



    }

    @Override
    public int getItemCount() {
        return imageUrls.size() >= numCards*numMatch ? numCards*numMatch:0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        ImageSwitcher imageSwitcher;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSwitcher = itemView.findViewById(R.id.card_image);
            imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
            imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_left));
            imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    return new ImageView(context.getApplicationContext());
                }
            });
        }
    }
}
