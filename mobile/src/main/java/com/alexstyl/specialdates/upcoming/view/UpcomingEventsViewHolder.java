package com.alexstyl.specialdates.upcoming.view;

import android.view.View;
import android.widget.TextView;

import com.alexstyl.specialdates.images.ImageLoader;
import com.alexstyl.specialdates.upcoming.UpcomingEventsViewModel;

class UpcomingEventsViewHolder extends UpcomingRowViewHolder<UpcomingEventsViewModel> {

    private final TextView dateView;
    private final UpcomingEventsView eventsView;
    private final ImageLoader imageLoader;

    UpcomingEventsViewHolder(View view,
                             TextView titleView,
                             UpcomingEventsView upcomingEventsView,
                             ImageLoader imageLoader) {
        super(view);
        this.dateView = titleView;
        this.eventsView = upcomingEventsView;
        this.imageLoader = imageLoader;
    }

    @Override
    public void bind(final UpcomingEventsViewModel viewModel, final OnUpcomingEventClickedListener listener) {
        dateView.setText(viewModel.getDisplayDateLabel());
        dateView.setTypeface(viewModel.getDateTypeFace());

        eventsView.bindBankHolidays(viewModel.getBankHolidayViewModel());
        eventsView.bindNamedays(viewModel.getNamedaysViewModel());
        eventsView.bindContactEvents(viewModel.getContactEventsViewModel(), listener, imageLoader);

        eventsView.setOnMoreButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardPressed(viewModel.getDate());
            }
        });
    }

    void playShowMeAnimation() {
        // TODO remove animation logic
    }
}