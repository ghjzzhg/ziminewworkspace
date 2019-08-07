/*
 * Copyright (c) Open Source Strategies, Inc.
 * 
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */


import javolution.util.FastList
import org.ofbiz.base.util.UtilDateTime

import java.text.DateFormat
import java.text.SimpleDateFormat

List latestnews = new FastList();
for (int i=0; i < 5; i++) {
    HashMap<String, String> news = new HashMap<String, String>();
    news.put("title", "信息" + i);
    news.put("link", "#");
    news.put("description", "测试信息发布");
    DateFormat dateFormat =  new SimpleDateFormat(UtilDateTime.DATE_FORMAT);
    String dateString = dateFormat.format(new Date());
    news.put("publishedDate", dateString);
    latestnews.add(news);
}
context.latestnews = latestnews;
