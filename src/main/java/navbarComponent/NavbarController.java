package navbarComponent;

import alerts.CustomAlertType;
import authentification.CurrentAccountSingleton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import controlPanelComponent.PreLoader;
import filterComponent.FilterSingleton;
import handlers.Convenience;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import listComponent.EventListSingleton;
import mainUI.MainPane;
import models.Account;
import models.Admin;
import sidebarComponent.SidebarController;
import sidebarComponent.SidebarState;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class which controls the navigation bar (toolbar)
 * @author Hidayat Rzayev
 */
public class  NavbarController implements Initializable {

    @FXML
    private AnchorPane navbarPane;

    @FXML
    private ImageView panelImageView;

    @FXML
    private ImageView refreshImageView;

    @FXML
    private ImageView searchImageView;

    @FXML
    private JFXTextField searchTextField;

    @FXML
    private SidebarController sidebarController;

    @FXML
    private JFXHamburger menuHamburger;

    private HamburgerSlideCloseTransition menuCloseTransition;
    private FilterSingleton filter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPanelImageView();
        initSearchTextField();
        initMenuButton();
        initSidebar();

        filter = FilterSingleton.getInstance();
    }

    /**
     * Initializes the panel image view depending on whether the logged-in user is admin or a regular user
     */
    private void initPanelImageView() {
        Account currentAccount = CurrentAccountSingleton.getInstance().getAccount();
        if (currentAccount instanceof Admin) {
            panelImageView.setVisible(true);
        }
    }

    /**
     * Initializes the search bar text field with the listener.
     * If the search bar is focused, it will be shown to the user.
     * If the search bar is not focused, but contains some text, it will also be shown to the user.
     * Otherwise, it will be hidden.
     */
    private void initSearchTextField() {
        searchTextField.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                showSearchTextField();
            } else {
                if (searchTextField.getText().isBlank()) {
                    hideSearchTextField();
                }
            }
        });
    }

    /**
     * Initializes the menu button that opens up the sidebar.
     */
    private void initMenuButton() {
        menuCloseTransition = new HamburgerSlideCloseTransition(menuHamburger);
        menuCloseTransition.setRate(-1);
    }

    /**
     * Initializes the sidebar according to its latest state (hidden or shown).
     */
    private void initSidebar() {
        boolean isSidebarHidden = SidebarState.getStateHidden();
        if (!isSidebarHidden) {
            sidebarController.show();
            menuCloseTransition.setRate(1);
            menuCloseTransition.play();
        }
    }

    /**
     * Opens the admin panel
     *
     * @param mouseEvent - the even which triggered the method
     */
    @FXML
    private void handlePanelClicked(MouseEvent mouseEvent) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/FXML/PreLoader.fxml"));
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setMaxHeight(200);
        dialogLayout.setMaxWidth(200);
        Parent root = (Parent) loader.load();
        PreLoader controller = (PreLoader) loader.getController();
        dialogLayout.setBody(root);
        BorderPane mainBorderPane = MainPane.getInstance().getBorderPane();
        BoxBlur blur = new BoxBlur(6, 6, 6);
        JFXDialog dialog = new JFXDialog(MainPane.getInstance().getStackPane(), dialogLayout, JFXDialog.DialogTransition.CENTER);
        dialog.setOnDialogClosed(event -> mainBorderPane.setEffect(null));
        dialog.setOnDialogOpened(event -> mainBorderPane.setEffect(blur));
        dialog.setOverlayClose(false);
        controller.setLoading(dialog);
        controller.initialization();
        dialog.show();
    }

    /**
<<<<<<< HEAD
     * If the sidebar is hidden, then it will be shown. Otherwise it will be hidden.
=======
     * Loads the Discussion page
     *
     * @param mouseEvent - the event which triggered the method
     */
    @FXML
    private void handleDiscussionClicked(MouseEvent mouseEvent) {
        try {
            Convenience.switchScene(mouseEvent, getClass().getResource("/FXML/discussionView.fxml"));
        } catch (IOException ioe) {
            Convenience.showAlert(CustomAlertType.ERROR, "Oops, something went wrong. Please, try again later.");
        }
    }

    /**
     * Opens the sidebar
>>>>>>> [DiscussionComponent] UI implemented
     *
     * @param mouseEvent - the event which triggered the method
     */
    @FXML
    private void handleMenuClicked(MouseEvent mouseEvent) {
        menuCloseTransition.setRate(menuCloseTransition.getRate() * -1);
        menuCloseTransition.play();

        if (sidebarController.isHidden()) {
            sidebarController.show();
        } else {
            sidebarController.hide();
        }

        SidebarState.saveStateHidden(sidebarController.isHidden());
    }

    /**
     * Refreshes the event list by getting the most recent data from the DB.
     *
     * @param mouseEvent - the event that triggered the method
     */
    @FXML
    private void handleRefreshClicked(MouseEvent mouseEvent){
        EventListSingleton eventList = EventListSingleton.getInstance();
        eventList.refreshList();

        refreshImageView.setDisable(true);
        rotateRefreshImageView();
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(5)
        );

        visiblePause.setOnFinished(
                (ActionEvent ev) -> {
                    refreshImageView.setDisable(false);
                }
        );
        visiblePause.play();
    }

    /**
     * Whenever the mouse is hovered over the search icon, the search bar will be shown to the user.
     *
     * @param mouseEvent - the event that triggered the method
     */
    @FXML
    private void handleSearchEntered(MouseEvent mouseEvent) {
        showSearchTextField();
    }

    /**
     * Whenever the mouse exits the search icon, it will be hidden from the user, provided
     * that the text in the search bar is blank and the search bar is not focused.
     *
     * @param mouseEvent - the event that triggered the method
     */
    @FXML
    private void handleSearchExited(MouseEvent mouseEvent) {
        if (searchTextField.getText().isBlank() && !searchTextField.isFocused()) {
            hideSearchTextField();
        }
    }

    /**
     * Whenever the mouse is hovered over the search text field, the search bar will be shown to the user.
     *
     * @param mouseEvent - the event that triggered the method
     */
    @FXML
    private void handleSearchTextFieldEntered(MouseEvent mouseEvent) {
        showSearchTextField();
    }

    /**
     * Whenever the mouse exits the search text field, the search bar will be hidden from the user, provided
     * that the text in the search bar is blank and the search bar is not focused.
     *
     * @param mouseEvent - the event that triggered the method
     */
    @FXML
    private void handleSearchTextFieldExited(MouseEvent mouseEvent) {
        if (searchTextField.getText().isBlank() && !searchTextField.isFocused()) {
            hideSearchTextField();
        }
    }

    /**
     * Whenever a user types something in the search bar, the filter for the {@link filterComponent.SearchCriteria}
     * class will be updated with the corresponding keyword.
     *
     * @param keyEvent - the event that triggered the method
     */
    @FXML
    private void handleSearchTextFieldTyped(KeyEvent keyEvent) {
        filter.setSearchKeyword(searchTextField.getText());
        filter.filterItems();
    }

    /**
     * Shows the search bar to the user.
     */
    private void showSearchTextField() {
        animateSearchField(90, 190);
    }

    /**
     * Hides the search bar from the user, clears the search bar and removes the focus from it.
     */
    private void hideSearchTextField() {
        animateSearchField(0, 0);
        searchTextField.clear();
        navbarPane.requestFocus();
    }

    /**
     * Animates the search bar to slide to open or close.
     * Animation lasts for 250 ms.
     *
     * @param searchIconAngle - the end angle of the search icon
     * @param textFieldWidth - the end width of the search text field
     */
    private void animateSearchField(double searchIconAngle, double textFieldWidth) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(searchTextField.prefWidthProperty(), searchTextField.getWidth()),
                        new KeyValue(searchImageView.rotateProperty(), searchImageView.getRotate())),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(searchTextField.prefWidthProperty(), textFieldWidth),
                        new KeyValue(searchImageView.rotateProperty(), searchIconAngle)));
        timeline.play();
    }

    private void rotateRefreshImageView() {
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setAxis(Rotate.Z_AXIS);
        rotateTransition.setByAngle(1080);
        rotateTransition.setDuration(Duration.millis(5000));
        rotateTransition.setNode(refreshImageView);
        rotateTransition.setAutoReverse(true);
        rotateTransition.play();
    }
}
