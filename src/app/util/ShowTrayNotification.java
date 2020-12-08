/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.util;

import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 *
 * @author Bright
 */
public class ShowTrayNotification {

    public ShowTrayNotification(String title, String message, NotificationType type) {
        TrayNotification notification = new TrayNotification();
        notification.setAnimationType(AnimationType.SLIDE);
        notification.setTray(title, message, type);
        notification.showAndDismiss(Duration.seconds(2));
    }

}
