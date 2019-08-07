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
import org.ofbiz.base.util.UtilMisc
/* @author: Michele Orru' (michele.orru@integratingweb.com) */

if(userLogin != null){
    partyId = userLogin.getString("partyId");
    partyAndPerson = delegator.findByPrimaryKeyCache("PartyAndPerson", UtilMisc.toMap("partyId", partyId));
    party = delegator.findByPrimaryKeyCache("Party", UtilMisc.toMap("partyId", partyId));

    context.put("firstName" , partyAndPerson.get("firstName"));
    context.put("lastName" , partyAndPerson.get("lastName"));
}
