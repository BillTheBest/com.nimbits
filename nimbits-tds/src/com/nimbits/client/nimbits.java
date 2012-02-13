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

package com.nimbits.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.nimbits.client.controls.MainMenuToolBar;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.NotLoggedInException;
import com.nimbits.client.exceptions.ObjectProtectionException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.LoginInfo;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.panels.*;
import com.nimbits.client.service.LoginService;
import com.nimbits.client.service.LoginServiceAsync;
import com.nimbits.client.service.blob.BlobService;
import com.nimbits.client.service.blob.BlobServiceAsync;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.client.service.recordedvalues.RecordedValueServiceAsync;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.settings.SettingsServiceAsync;
import com.nimbits.client.service.twitter.TwitterService;
import com.nimbits.client.service.twitter.TwitterServiceAsync;
import com.nimbits.shared.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class nimbits implements EntryPoint {

    private MainPanel mainPanel;
    private LoginInfo loginInfo = null;
    private Viewport viewport;
    private final static String heading = (Const.CONST_SERVER_NAME + " " + Const.CONST_SERVER_VERSION);
    private ClientType clientType;



    private void loadLayout(final LoginInfo loginInfo,
                            final Action action,
                            final Map<String, String> settings,
                            final String uuid)  {

        final ContentPanel contentPanel = new ContentPanel(new FillLayout());


        final String logoutUrl = (loginInfo != null) ? loginInfo.getLogoutUrl() : Const.PATH_NIMBITS_HOME;


        final boolean loadConnections = (settings != null && settings.containsKey(Const.SETTING_ENABLE_CONNECTIONS)
                && settings.get(Const.SETTING_ENABLE_CONNECTIONS).equals("1"));


        viewport = new Viewport();
        if (action.equals(Action.android)) {
            viewport.setLayout(new FillLayout());
            viewport.setBorders(false);
            mainPanel = new MainPanel(loginInfo, true, settings);
            contentPanel.add(mainPanel);
            contentPanel.setHeaderVisible(false);
            contentPanel.setLayout(new FillLayout());
            viewport.add(contentPanel);
        }
        else {
            viewport.setLayout(new BorderLayout());
            viewport.setBorders(false);

            contentPanel.setHeaderVisible(true);
            MainMenuToolBar toolBar = new MainMenuToolBar(logoutUrl, loginInfo, settings);
            toolBar.addReloadListener(new NavigationEventProvider.ReloadListener() {
                @Override
                public void onReload() {
                    contentPanel.removeAll();
                    mainPanel = new MainPanel(loginInfo, false, settings);
                    contentPanel.add(mainPanel);
                    viewport.layout(true);

                }
            });
            contentPanel.setTopComponent(toolBar);

            if (loginInfo != null) {
                contentPanel.setHeading(heading + " " + loginInfo.getEmailAddress().getValue());
            }

            mainPanel = new MainPanel(loginInfo, false, settings);
            contentPanel.add(mainPanel);
            contentPanel.setLayout(new FillLayout());
            addListeners();
            viewport.add(contentPanel, new BorderLayoutData(LayoutRegion.CENTER));
            if (action.equals(Action.subscribe)) {
                Cookies.removeCookie(Action.subscribe.name());
                 showSubscriptionPanel(uuid, settings);
            }
        }


        viewport.setHeight("100%");
        RootPanel.get("main").add(viewport);

    }
    public void showSubscriptionPanel(final String uuid, final Map<String, String> settings) {

        EntityServiceAsync service = GWT.create(EntityService.class);

        service.getEntityByUUID(uuid, new AsyncCallback<Entity>() {
            @Override
            public void onFailure(Throwable caught) {
                //auto generated
            }

            @Override
            public void onSuccess(Entity result) {
                SubscribePanel dp = new SubscribePanel(result, settings);

                final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
                w.setWidth(500);
                w.setHeight(500);
                w.setHeading("Subscribe");
                w.add(dp);
                dp.addSubscriptionAddedListener(new NavigationEventProvider.SubscriptionAddedListener() {
                    @Override
                    public void onSubscriptionAdded(Entity model) {
                        w.hide();
                        Cookies.removeCookie(Action.subscribe.name());
                      //  mainPanel.addEntity(result);
                     //TODO   mainPanel.addEnToTree(result);

                    }
                });

                w.show();
            }
        });




    }
    private void loadDiagramView(final Entity diagram,
                                 final ClientType clientType) {

        viewport = new Viewport();
        viewport.setLayout(new BorderLayout());
        viewport.setBorders(false);

        final ContentPanel contentPanel = new ContentPanel(new FillLayout());
        contentPanel.setHeaderVisible(true);
        contentPanel.setHeading(Const.HTML_HOME_LINK + " | " + heading + " "
                + diagram.getName());

      //  diagram.setFullScreenView(true);

        final DiagramPanel diagramPanel = new DiagramPanel(diagram, false, Window.getClientWidth(), Window.getClientHeight());
        diagramPanel.addEntityClickedListeners(new NavigationEventProvider.EntityClickedListener() {

            @Override
            public void onEntityClicked(final Entity p) {

                if (clientType == ClientType.other) {
                    switch (p.getEntityType()) {
                        case point:
                          //TODO  showAnnotatedTimeLine(p);
                        case diagram:
                         //TODO  loadDiagramView(d, clientType);
                    }

                } else {
                    Window.Location.replace("?" + Const.PARAM_CLIENT + "=" + Const.WORD_ANDROID + "&" + Const.PARAM_POINT + "=" + p.getName());
                }

            }

        });



        diagramPanel.addUrlClickedListeners(new NavigationEventProvider.UrlClickedListener() {

            @Override
            public void onUrlClicked(String url, String target) {
                Window.Location.replace(url);
            }
        });
        diagramPanel.setHeight("100%");
        contentPanel.add(diagramPanel);
        contentPanel.setLayout(new FillLayout());
        viewport.add(contentPanel, new BorderLayoutData(LayoutRegion.CENTER));
        RootPanel.get().add(viewport);

    }

    void showAnnotatedTimeLine(final Point point) {

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
     //   final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);
        final ContentPanel p = new ContentPanel();
        p.setHeading(point.getName().getValue());

       // final List<Point> points = Arrays.asList(point);
        //the chart panel will determine the end date for the first show
        final AnnotatedTimeLinePanel annotatedTimeLinePanel = new AnnotatedTimeLinePanel(false, Const.DEFAULT_CHART_NAME);
        //  final Date start = new Date(result.getTime() - (1000 * 60 * 60 * 24) );
        // annotatedTimeLinePanel.setTimespan(new TimespanModel(start, result));
        //  annotatedTimeLinePanel.setPoints(points);

        p.add(annotatedTimeLinePanel);
        p.setWidth(600);
        p.setHeight(400);
        annotatedTimeLinePanel.initChart();
        annotatedTimeLinePanel.addPoint(point);
        w.add(p);
        w.setHeight(400);
        w.setWidth(600);
        w.show();

//        dataService.getLastRecordedDate(points, new AsyncCallback<Date>() {
//            @Override
//            public void onFailure(Throwable caught) {
//
//            }
//
//            @Override
//            public void onSuccess(final Date result) {
//
//
//            }
//
//
//        });
    }

    private void addListeners() {

        mainPanel.addEntityClickedListeners(new NavigationEventProvider.EntityClickedListener() {
            //need to getInstance a fresh copy here
            @Override
            public void onEntityClicked(final Entity c) {
                switch (c.getEntityType()) {
                    case category:
                        //TODO categoryClicked(c);
                        break;
                    case point:
                        //TODO mainPanel.addPoint(c);
                        break;
                    case diagram:
                       //TODO mainPanel.addDiagram(d);
                        break;
                }


            }
        });



    }


    @Override
    public void onModuleLoad() {
        final String clientTypeParam = Location.getParameter(Const.PARAM_CLIENT);
        GWT.log("onModuleLoad");

        String uuid = Location.getParameter(Const.PARAM_UUID);
        final String actionParam = Location.getParameter(Const.PARAM_ACTION);
        final String fb = Location.getParameter(Const.PARAM_FACEBOOK);
        final String code = Location.getParameter(Const.PARAM_CODE);
        final String tw = Location.getParameter(Const.PARAM_TWITTER);
        final String oauth_token = Location.getParameter(Const.PARAM_OAUTH);
        final String diagramUUID = Location.getParameter(Const.PARAM_DIAGRAM);

//        final String debug = Location.getParameter(Const.PARAM_DEBUG);
        boolean doAndroid = false;

        final boolean doFacebook = ((fb != null) || (code != null));
        final boolean doTwitter = ((tw != null) && (oauth_token == null));
        final boolean doTwitterFinish = ((tw != null) && (oauth_token != null));
        final boolean doDiagram = (diagramUUID != null);
        boolean doSubscribe = (uuid != null && actionParam != null && actionParam.equals(Action.subscribe.name()));
        Action action = Action.none;

        if (Cookies.getCookieNames().contains(Const.PARAM_CLIENT) && Utils.isEmptyString(clientTypeParam)) {
            clientType = ClientType.valueOf(Cookies.getCookie(Const.PARAM_CLIENT));
        }
        else if (!Utils.isEmptyString(clientTypeParam) && clientTypeParam.equals(Const.WORD_ANDROID)) {
            clientType = ClientType.android;
            doAndroid = true;
        } else {
            clientType = ClientType.other;
        }

        //handles the round trip from login screen.

        if (doSubscribe && ! Cookies.getCookieNames().contains(Action.subscribe.name())) {

            action = Action.subscribe;
        }
        else if (! doSubscribe && Cookies.getCookieNames().contains(Action.subscribe.name())) {
            uuid = Cookies.getCookie(Action.subscribe.name());
            Cookies.removeCookie(Action.subscribe.name());
            action = Action.subscribe;
        }
        else if (uuid != null && ! doSubscribe) {
            action = Action.report;
        }
        else if (doAndroid) {
            action = Action.android;
        }
        else if (doDiagram) {
            action = Action.diagram;
        }
        else if (doTwitter) {
            action = Action.twitter;
        }
        else if (doTwitterFinish) {
            action = Action.twitterFinishReg;
        }
        else if (doFacebook) {
            action = Action.facebook;
        }

        Cookies.setCookie(Const.PARAM_CLIENT, clientType.name());
        loadPortalView(uuid, code, oauth_token, action);

    }


    private void processDiagramRequest(final String diagramName, final ClientType clientType) {
        BlobServiceAsync diagramService = GWT.create(BlobService.class);

        EntityServiceAsync service = GWT.create(EntityService.class);

        service.getEntityByUUID(diagramName, new AsyncCallback<Entity>() {
            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess(Entity entity) {
                loadDiagramView(entity, clientType);
            }
        });

//        diagramService.getDiagramByUuid(diagramName, new AsyncCallback<Diagram>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                handleError(throwable);
//            }
//
//            @Override
//            public void onSuccess(final Diagram diagram) {
//
//            }
//        });

    }


    private void loadPortalView(final String uuid,
                                final String code,
                                final String oauth_token,
                                final Action action){
        SettingsServiceAsync settingService = GWT.create(SettingsService.class);
        settingService.getSettings(new AsyncCallback<Map<String, String>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
                Window.Location.replace(loginInfo.getLogoutUrl());
            }

            @Override
            public void onSuccess(final Map<String, String> settings) {
                switch (action) {
                    case report:
                        loadSinglePointDisplay(uuid);
                        break;
                    case diagram:
                        processDiagramRequest(uuid, clientType);
                        break;
                    case facebook:
                        finishFacebookAuthentication(settings, code);
                        break;
                    case twitterFinishReg:
                        finishTwitterAuthentication(settings, oauth_token, action);
                        break;
                    case subscribe:
                        loadPortal(action, settings, uuid);
                        break;

                    case none:
                        loadPortal(action, settings, uuid);
                        break;
                    default:
                        loadLogin();
                }
            }

        });
    }




    private void finishFacebookAuthentication(final Map<String, String> settings, final String code) {
        getViewport();
        FacebookPanel fbPanel = new FacebookPanel(code, settings);
        fbPanel.setHeight(500);
        fbPanel.setWidth(600);
        viewport.add(fbPanel);
        viewport.setWidth(600);
        viewport.setHeight(500);
        RootPanel.get("main").add(viewport);
    }

    private void finishTwitterAuthentication(final Map<String, String> settings, final String oauth_token, final Action action) {
        TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
        twitterService.updateUserToken(oauth_token,
                new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        handleError(caught);

                    }

                    @Override
                    public void onSuccess(Void result) {
                        Window.alert(Const.MESSAGE_TWITTER_ADDED);
                        loadPortal(action, settings, null);

                    }

                });

    }

    private void getViewport() {
        viewport = new Viewport();
        viewport.setLayout(new FillLayout());
        viewport.setBorders(false);
    }

    private void loadSinglePointDisplay(final String uuid) {
        Location.replace("report.html?uuid=" + uuid);
    }

    private void loadPortal(final Action action, final Map<String, String> settings, final String uuid)   {
        LoginServiceAsync loginService = GWT
                .create(LoginService.class);
        loginService.login(GWT.getHostPageBaseURL(),
                new AsyncCallback<LoginInfo>() {
                    @Override
                    public void onFailure(Throwable error) {
                        GWT.log(error.getMessage(), error);
                        handleError(error);
                    }

                    @Override
                    public void onSuccess(LoginInfo result) {
                        loginInfo = result;
                        if (loginInfo.isLoggedIn()) {
                            switch (action) {
                                case android: case none:
                                    loadLayout(loginInfo, action, settings, uuid);
                                    break;
                                case twitter:
                                    final TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
                                    twitterService.twitterAuthorise(loginInfo.getEmailAddress(), new AsyncCallback<String>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                            GWT.log(caught.getMessage(), caught);
                                        }

                                        @Override
                                        public void onSuccess(String result) {

                                            Location.replace(result);
                                        }

                                    });
                                    break;
                                case subscribe:

                                    loadLayout(loginInfo, action, settings, uuid);
                                    break;

                                default:

                                    loadLogin();

                            }


                        } else {
                            if (action.equals(Action.subscribe)) {
                                Cookies.setCookie(Action.subscribe.name(), uuid);
                            }
                            loadLogin();
                        }
                    }

                });
    }

    private void loadData(final Point point) throws NimbitsException {
        final List<Point> points = new ArrayList<Point>();
        final ContentPanel mainContentPanel = new ContentPanel();
        points.add(point);
        getViewport();

        mainContentPanel.setHeading(point.getName() + "  " + point.getDescription());
        viewport.add(mainContentPanel);
        RootPanel.get("main").add(viewport);
        LoginServiceAsync loginService = GWT.create(LoginService.class);

        loginService.login(GWT.getHostPageBaseURL(),
                new AsyncCallback<LoginInfo>() {
                    @Override
                    public void onFailure(Throwable error) {
                        handleError(error);
                    }

                    @Override
                    public void onSuccess(LoginInfo result) {
                        loginInfo = result;
                        if ((loginInfo.isLoggedIn() && loginInfo.getUser().getId() == point.getUserFK()) || point.isPublic()) {
                            loadChart(points, mainContentPanel);
                        } else {
                            loadLogin();
                        }
                    }

                });


    }

    private void loadChart(final List<Point> points, final ContentPanel p) {
        RecordedValueServiceAsync dataService;
        dataService = GWT.create(RecordedValueService.class);

        dataService.getLastRecordedDate(points, new AsyncCallback<Date>() {

            @Override
            public void onFailure(Throwable caught) {


            }

            @Override
            public void onSuccess(Date result) {

                final AnnotatedTimeLinePanel annotatedTimeLinePanel = new AnnotatedTimeLinePanel(false, Const.DEFAULT_CHART_NAME);
                // annotatedTimeLinePanel.setPoints(points);
                p.add(annotatedTimeLinePanel);
                p.setWidth(viewport.getWidth());
                p.setHeight(viewport.getHeight());
                annotatedTimeLinePanel.initChart();
                viewport.layout(true);
            }
        });
    }

    private void loadLogin() {

        Window.Location.replace(loginInfo.getLoginUrl());

    }

    private void handleError(Throwable error) {


        if (error instanceof NotLoggedInException) {
            Window.Location.replace(loginInfo.getLogoutUrl());
        } else if (error instanceof ObjectProtectionException) {
            Window.Location.replace(Const.PATH_OBJECT_PROTECTION_URL);
        } else {
            Window.alert(error.getMessage());

            // Window.Location.replace(Const.PATH_NIMBITS_HOME);
        }
    }
}
