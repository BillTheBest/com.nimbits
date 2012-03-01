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

package com.nimbits.server.task;

import com.google.gson.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.logging.*;

public class PointMaintTask extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointMaintTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final Gson gson = GsonFactory.getInstance();
        resp.setContentType(Const.CONTENT_TYPE_HTML);

        final String pointJson = req.getParameter(Const.Params.PARAM_POINT);
        final PointModel p = gson.fromJson(pointJson, PointModel.class);
        // final UserContext context = gson.fromJson(jsonContext, UserContextImpl.class);


        User n = null;  // pm.getObjectById(NimbitsUser.class, p.getUserFK());//UserDAL.getNimbitsUserByID(p.getUser());
        try {
            n = UserTransactionFactory.getInstance().getNimbitsUserByID(p.getUserFK());
        } catch (Exception e) {
           log.severe(e.getMessage());
        }


        if (n != null) {
           // if (p.getCatID() != 0) {
           // Category category = CategoryServiceFactory.getInstance().getCategory(n, p.getCatID());
         //   try {
               // PointTransactionsFactory.getInstance(null).checkPoint(req, n.getEmail(), p);


           // } catch (NimbitsException e) {
            //    log.severe(e.getMessage());
            //}

            ///log.info("reporting point to core:" + p.getName().getValue());
           // String url = ServerInfoImpl.getFullServerURL(req);
           // CoreFactory.getInstance().reportUpdateToCore(url, pointJson, EntityType.point);









        } else {



        }


    }


}

