/*
 * KINGSTAR MEDIA SOLUTIONS Co.,LTD. Copyright c 2005-2013. All rights reserved.
 *
 * This source code is the property of KINGSTAR MEDIA SOLUTIONS LTD. It is intended
 * only for the use of KINGSTAR MEDIA application development. Reengineering, reproduction
 * arose from modification of the original source, or other redistribution of this source
 * is not permitted without written permission of the KINGSTAR MEDIA SOLUTIONS LTD.
 */
package me.chanjar.weixin.enterprise.util.json;

import java.lang.reflect.Type;

import me.chanjar.weixin.common.GsonHelper;
import me.chanjar.weixin.enterprise.bean.WxCpMenu;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 
 * @author Daniel Qian
 *
 */
public class WxCpMenuGsonAdapter implements JsonSerializer<WxCpMenu>, JsonDeserializer<WxCpMenu> {

  public JsonElement serialize(WxCpMenu menu, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject json = new JsonObject();

    JsonArray buttonArray = new JsonArray();
    for (WxCpMenu.WxMenuButton button : menu.getButtons()) {
      JsonObject buttonJson = convertToJson(button);
      buttonArray.add(buttonJson);
    }
    json.add("button", buttonArray);
    
    return json;
  }

  protected JsonObject convertToJson(WxCpMenu.WxMenuButton button) {
    JsonObject buttonJson = new JsonObject();
    buttonJson.addProperty("type", button.getType());
    buttonJson.addProperty("name", button.getName());
    buttonJson.addProperty("key", button.getKey());
    buttonJson.addProperty("url", button.getUrl());
    if (button.getSubButtons() != null && button.getSubButtons().size() > 0) {
      JsonArray buttonArray = new JsonArray();
      for (WxCpMenu.WxMenuButton sub_button : button.getSubButtons()) {
        buttonArray.add(convertToJson(sub_button));
      }
      buttonJson.add("sub_button", buttonArray);
    }
    return buttonJson;
  }

  public WxCpMenu deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    /*
     * 操蛋的微信
     * 创建菜单时是 { button : ... }
     * 查询菜单时是 { menu : { button : ... } }
     */
    WxCpMenu menu = new WxCpMenu();
    JsonObject menuJson = json.getAsJsonObject().get("menu").getAsJsonObject();
    JsonArray buttonsJson = menuJson.get("button").getAsJsonArray();
    for (int i = 0; i < buttonsJson.size(); i++) {
      JsonObject buttonJson = buttonsJson.get(i).getAsJsonObject();
      WxCpMenu.WxMenuButton button = convertFromJson(buttonJson);
      menu.getButtons().add(button);
      if (buttonJson.get("sub_button") == null || buttonJson.get("sub_button").isJsonNull()) {
        continue;
      }
      JsonArray sub_buttonsJson = buttonJson.get("sub_button").getAsJsonArray();
      for (int j = 0; j < sub_buttonsJson.size(); j++) {
        JsonObject sub_buttonJson = sub_buttonsJson.get(j).getAsJsonObject();
        button.getSubButtons().add(convertFromJson(sub_buttonJson));
      }
    }
    return menu;
  }
  
  protected WxCpMenu.WxMenuButton convertFromJson(JsonObject json) {
    WxCpMenu.WxMenuButton button = new WxCpMenu.WxMenuButton();
    button.setName(GsonHelper.getString(json, "name"));
    button.setKey(GsonHelper.getString(json, "key"));
    button.setUrl(GsonHelper.getString(json, "url"));
    button.setType(GsonHelper.getString(json, "type"));
    return button;
  }

}
