/*
 * FileUploadBean.java
 *
 * Created on February 8, 2006, 9:07 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.javaee.blueprints.petstore.controller;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.http.HttpServletResponse;

import com.sun.javaee.blueprints.petstore.model.Item;
import com.sun.javaee.blueprints.components.ui.fileupload.FileUploadStatus;
/**
 *
 * @author basler
 */
public class FileUploadBean {
    @PersistenceContext(name="bppu")
    private static EntityManager em;
    @Resource UserTransaction utx;
    private boolean bDebug=false;
    
    /** Creates a new instance of FileUploadBean */
    public FileUploadBean() {
    }
    
    public void postProcessingMethod(PhaseEvent event, HashMap hmUpload, FileUploadStatus status) {
        if(bDebug) System.out.println("IN Custom Post Processing method");
        try {
            // set custom return enabled so Phaselistener knows not to send default response
            status.enableCustomReturn();
            FacesContext context=event.getFacesContext();
            HttpServletResponse response=(HttpServletResponse)context.getExternalContext().getResponse();
            //persist the data
            try{
                String fileNameKey = null;
                Set keySet = hmUpload.keySet();
                Iterator iter = keySet.iterator();
                while(iter.hasNext()){
                    String key = iter.next().toString();
                    if(key.startsWith("fileLocation"))
                        fileNameKey = key;
                }
                String absoluteFileName = hmUpload.get(fileNameKey).toString();
                String fileName = null;
                int lastSeparator = absoluteFileName.lastIndexOf("/") + 1;
                if (lastSeparator != -1) {
                    fileName = absoluteFileName.substring(lastSeparator, absoluteFileName.length());
                }
                Item item = new Item();
                String itemId = hmUpload.get("item").toString();
                String prodId = hmUpload.get("product").toString();
                String name = hmUpload.get("name").toString();
                String desc = hmUpload.get("description").toString();
                String unitCost = hmUpload.get("unitCost").toString();
                String listPrice = hmUpload.get("listPrice").toString();
                item.setItemID(itemId);
                item.setProductID(prodId);
                item.setDescription(desc);
                item.setName(name);
                item.setUnitCost(new Float(unitCost));
                item.setListPrice(new Float(listPrice));
                item.setImageURL(fileName);
                utx.begin();
                em.persist(item);
                utx.commit();
            }catch (Exception ex) {
                System.out.println("Error persisting seller data: "+ex);
                try {
                    utx.rollback();
                } catch (Exception exe) {
                    System.out.println("Persisting seller data, rollback failed: "+exe.getMessage());
                }
            }
            StringBuffer sb=new StringBuffer();
            response.setContentType("text/xml;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            sb.append("<response>");
            sb.append("<message>***CUSTOM SERVER-SIDE RETURN *** MESSAGE->");
            sb.append(status.getMessage());
            sb.append("</message>");
            sb.append("<status>");
            sb.append(status.getStatus());
            sb.append("</status>");
            sb.append("<duration>");
            sb.append(status.getUploadTime());
            sb.append("</duration>");
            sb.append("<duration_string>");
            sb.append(status.getUploadTimeString());
            sb.append("</duration_string>");
            sb.append("<start_date>");
            sb.append(status.getStartUploadDate());
            sb.append("</start_date>");
            sb.append("<end_date>");
            sb.append(status.getEndUploadDate());
            sb.append("</end_date>");
            sb.append("<upload_size>");
            sb.append(status.getTotalUploadSize());
            sb.append("</upload_size>");
            sb.append("</response>");
            if(bDebug) System.out.println("Response:\n" + sb);
            response.getWriter().write(sb.toString());
            
        } catch (IOException iox) {
            System.out.println("FileUploadPhaseListener error writting AJAX response : " + iox);
        }
    }
    
}