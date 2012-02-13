/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;

import java.util.ArrayList;
import java.util.List;

public abstract class NavigationEventProvider extends LayoutContainer {

    private final List<EntityDeletedListener> entityDeletedListeners = new ArrayList<EntityDeletedListener>();
    private final List<ReloadListener> reloadListeners = new ArrayList<ReloadListener>();


    private final List<EntityClickedListener> entityClickedListeners = new ArrayList<EntityClickedListener>();
    private final List<UrlClickedListener> urlClickedListeners = new ArrayList<UrlClickedListener>();
    private final List<ValueEnteredListener> valueEnteredListeners = new ArrayList<ValueEnteredListener>();
    private final List<ChartRemovedListener> chartRemovedListeners = new ArrayList<ChartRemovedListener>();

    private final List<SubscriptionAddedListener> subscriptionAddedListeners = new ArrayList<SubscriptionAddedListener>();


    public interface ReloadListener {
        void onReload();
    }


    public void notifyReloadListener()  {
        for (ReloadListener l : reloadListeners) {
            l.onReload();
        }
    }
    public void addReloadListener(final ReloadListener listener) {
        reloadListeners.add(listener);
    }
    public interface SubscriptionAddedListener {
        void onSubscriptionAdded(final Entity model);
    }

    public void addSubscriptionAddedListener(final SubscriptionAddedListener listener) {
        subscriptionAddedListeners.add(listener);
    }


    void notifySubscriptionAddedListener(final Entity model)  {
        for (SubscriptionAddedListener SubscriptionAddedListener : subscriptionAddedListeners) {
            SubscriptionAddedListener.onSubscriptionAdded(model);
        }
    }


    void addEntityDeletedListeners(final EntityDeletedListener listener) {
        entityDeletedListeners.add(listener);
    }

    void notifyEntityDeletedListener(final Entity entity)  {
        for (EntityDeletedListener listener : entityDeletedListeners) {
            listener.onEntityDeleted(entity);
        }
    }

    public interface EntityDeletedListener {
        void onEntityDeleted(final Entity entity) ;

    }

    // ChartRemoved Click Handlers
    public interface ChartRemovedListener {
        void onChartRemovedClicked(final String chartName);
    }

    void addChartRemovedClickedListeners(final ChartRemovedListener listener) {
        chartRemovedListeners.add(listener);
    }

    void notifyChartRemovedListener(final String chartName) {
        for (ChartRemovedListener ChartRemovedClickedListener : chartRemovedListeners) {
            ChartRemovedClickedListener.onChartRemovedClicked(chartName);
        }
    }

    // Value entered Handlers
    public interface ValueEnteredListener {
        void onValueEntered(final Entity point, final Value value);

    }

    void addValueEnteredListeners(final ValueEnteredListener listener) {
        valueEnteredListeners.add(listener);
    }

    void notifyValueEnteredListener(final Entity point, final Value value) {
        for (final ValueEnteredListener valueEnteredListener : valueEnteredListeners) {
            valueEnteredListener.onValueEntered(point, value);
        }
    }

    // Point Click Handlers
    public interface EntityClickedListener {
        void onEntityClicked(final Entity entity);

    }

    public void addEntityClickedListeners(final EntityClickedListener listener) {
        entityClickedListeners.add(listener);
    }

    void notifyEntityClickedListener(final Entity entity)  {

        for (EntityClickedListener clickedListener :entityClickedListeners) {
            clickedListener.onEntityClicked(entity);
        }
    }


    // Diagram Click Handlers
    public interface UrlClickedListener {
        void onUrlClicked(final String url, final String target);

    }

    public void addUrlClickedListeners(final UrlClickedListener listener) {
        urlClickedListeners.add(listener);
    }

    void notifyUrlClickedListener(final String url, final String target) {
        for (UrlClickedListener urlClickedListener : urlClickedListeners) {
            urlClickedListener.onUrlClicked(url, target);
        }
    }








}
