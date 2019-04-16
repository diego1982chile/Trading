package cl.dsoto.trading;

import cl.dsoto.trading.clients.ServiceLocator;
import cl.dsoto.trading.components.PeriodManager;
import cl.dsoto.trading.model.Period;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by des01c7 on 03-04-19.
 */
@ManagedBean
@SessionScoped
public class MenuBean {

    //@EJB
    private PeriodManager periodManager = (PeriodManager) ServiceLocator.getInstance().getService(PeriodManager.class);

    private transient MenuModel menuModel;

    /**
     * Lista de categorías para el despliegue del filtro por categorías
     */
    private List<Period> periods = new ArrayList<>();

    @PostConstruct
    protected void initialize() {

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            periods = periodManager.getLast(100);
        }
        catch (Exception e) {

        }

        initMenu();

    }

    public MenuModel getMenuModel() {
        return menuModel;
    }

    public void setMenuModel(MenuModel menuModel) {
        this.menuModel = menuModel;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    public void initMenu() {

        menuModel = new DefaultMenuModel();

        //Inicio
        DefaultMenuItem item0 = new DefaultMenuItem("Inicio");
        item0.setUrl("/views/home");
        item0.setIcon("home");
        item0.setId("rm_home");

        menuModel.addElement(item0);

        //Categorias
        DefaultSubMenu backtestSubmenu = new DefaultSubMenu("BackTests");
        backtestSubmenu.setIcon("fa fa-list-alt");
        backtestSubmenu.setId("rm_categories");

        for (Period period : periods) {
            DefaultMenuItem item = new DefaultMenuItem(period.getName());
            item.setUrl("/views/period/"+period.getId());
            item.setIcon("fa fa-list-alt");
            item.setStyleClass("loader-trigger");
            item.setId("rm_"+period.getName());
            item.setStyleClass("loader-trigger");
            item.setOncomplete("unloader();");
            //item.setCommand("#{mainMenuBean.redirect}");
            //item.setParam("idCategory",category.getId());
            //item.setAjax(true);
            item.setUpdate("form");
            backtestSubmenu.addElement(item);
        }

        menuModel.addElement(backtestSubmenu);

    }
}
