package com.ruslaniusupov.achievity.data;

import android.support.annotation.NonNull;

import java.util.List;

public interface SubscriptionsPrefsDataSource {

    void add(@NonNull String subscriptionId);

    void delete(@NonNull String subscriptionId);

    boolean contains(@NonNull String subscriptionId);

    void batchAdd(List<String> subscriptionIds);

}
