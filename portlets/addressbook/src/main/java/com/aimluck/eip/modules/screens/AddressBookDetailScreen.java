/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.addressbook.AbstractAddressBookFilterdSelectData;
import com.aimluck.eip.addressbook.AddressBookCorpFilterdSelectData;
import com.aimluck.eip.addressbook.AddressBookFilterdSelectData;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳の会社情報
 * 
 */
public class AddressBookDetailScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookDetailScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    try {
      AbstractAddressBookFilterdSelectData<?, ?> detailData = null;
      String selectedTab = rundata.getParameters().getString("tab");
      if ("corp".equals(selectedTab)) {
        detailData = new AddressBookCorpFilterdSelectData();
      } else {
        detailData = new AddressBookFilterdSelectData();
      }
      detailData.initField();
      detailData.doViewDetail(this, rundata, context);

      if (!detailData.checkHasAuthority(
        rundata,
        ALAccessControlConstants.VALUE_ACL_DETAIL)) {
        throw new ALPermissionException();
      }

      String entityid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      context.put(ALEipConstants.ENTITY_ID, entityid);
      String layout_template = "portlets/html/ja/ajax-addressbook-detail.vm";

      setTemplate(rundata, context, layout_template);
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
    } catch (Exception ex) {
      logger.error("AddressBookDetailScreen.doOutput", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return AddressBookUtils.ADDRESSBOOK_PORTLET_NAME;
  }
}
