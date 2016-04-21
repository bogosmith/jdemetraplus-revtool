/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.descriptors;

import ec.nbdemetra.ra.model.CointegrationDetailType;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.specification.VARModelsSpecification;
import ec.nbdemetra.ra.utils.StringUtils;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author aresda
 */
public class VARModelsSpecUI implements IPropertyDescriptors {

    final VARModelsSpecification core;
    final boolean ro;
    private static final int DF_ID = 0, ADF_ID = 1, ADF_LAG_ID = 2, DFTI_ID = 3, DFTI_LAG_ID = 4, PP_ID = 5,
            VECM_ID = 6, VECM_ORDER_ID = 7, VECM_RANK_ID = 8, BG_TEST_ID = 9,
            BG_ORDER_ID = 10, LB_TEST_ID = 11, LB_ORDER_ID = 11, COINT_ID = 13, COINT_ORDER_ID = 14, COINT_DETAIL_ID = 15;
    private static final String DF_NAME = MethodName.DF.toString(),
            ADF_NAME = MethodName.ADF.toString(), ADF_LAG_NAME = MethodName.ADF_LAG.toString(),
            DFTI_NAME = MethodName.DFTI.toString(), DFTI_LAG_NAME = MethodName.DFTI_LAG.toString(),
            PP_NAME = MethodName.PHILIPS_PERRON.toString(),
            VECM_NAME = MethodName.VECM.toString(), VECM_ORDER_NAME = "VECM-AutoRegressive order", VECM_RANK_NAME = "VECM-Integration rank",
            BG_TEST_NAME = MethodName.BG_TEST.toString(), BG_ORDER_NAME = "BG-AutoRegressive order",
            LB_TEST_NAME = MethodName.LB_TEST.toString(), LB_ORDER_NAME = "LB-AutoRegressive order",
            COINT_NAME = MethodName.COINT.toString(), COINT_ORDER_NAME = "Cointegration-Difference of residual order",
            COINT_DETAIL_NAME = MethodName.COINT_DETAIL.toString();
    private static final String DF_DESC = MethodName.DF.toString(),
            ADF_DESC = MethodName.ADF.toString(), ADF_LAG_DESC = ADF_LAG_NAME.concat(" (1 or 2)"),
            DFTI_DESC = MethodName.DFTI.toString(), DFTI_LAG_DESC = DFTI_LAG_NAME.concat(" (1 or 2)"),
            PP_DESC = MethodName.PHILIPS_PERRON.toString(),
            VECM_DESC = MethodName.VECM.toString(), VECM_ORDER_DESC = VECM_ORDER_NAME.concat(" (1 or 2)"), VECM_RANK_DESC = VECM_RANK_NAME.concat(" (0 or 1)"),
            BG_TEST_DESC = MethodName.BG_TEST.toString(), BG_ORDER_DESC = BG_ORDER_NAME.concat(" (1 or 2)"),
            LB_TEST_DESC = MethodName.LB_TEST.toString(), LB_ORDER_DESC = LB_ORDER_NAME.concat(" (1 or 2)"),
            COINT_DESC = MethodName.COINT.toString(), COINT_ORDER_DESC = COINT_ORDER_NAME.concat(" (0 or 1)"),
            COINT_DETAIL_DESC = "Cointegration-".concat(MethodName.COINT_DETAIL.toString());

    public VARModelsSpecUI(VARModelsSpecification core, boolean ro) {
        this.core = core;
        this.ro = ro;
    }

    public String getDisplayName() {
        return "VARModelsSpecUI";
    }

    public boolean isDf() {
        return this.core.contains(MethodName.DF);
    }

    public void setDf(boolean bool) {
        if (bool) {
            core.add(MethodName.DF);
        } else {
            core.remove(MethodName.DF);
        }
    }

    public boolean isAdf() {
        return this.core.contains(MethodName.ADF);
    }

    public void setAdf(boolean bool) {
        //core.setAdfLag(bool ? VARModelsSpecification.DEF1 : 0);
        if (bool) {
            core.add(MethodName.ADF);
        } else {
            core.remove(MethodName.ADF);
        }
    }

    public int getAdfLag() {
        return core.getAdfLag();
    }

    public void setAdfLag(int value) {
        this.core.setAdfLag(value);
    }

    public boolean isDfti() {
        return this.core.contains(MethodName.DFTI);
    }

    public void setDfti(boolean bool) {
        //core.setAdfLag(bool ? VARModelsSpecification.DEF1 : 0);
        if (bool) {
            core.add(MethodName.DFTI);
        } else {
            core.remove(MethodName.DFTI);
        }
    }

    public int getDftiLag() {
        return core.getDftiLag();
    }

    public void setDftiLag(int value) {
        this.core.setDftiLag(value);
    }

    public boolean isPp() {
        return this.core.contains(MethodName.PHILIPS_PERRON);
    }

    public void setPp(boolean bool) {
        //core.setAdfLag(bool ? VARModelsSpecification.DEF1 : 0);
        if (bool) {
            core.add(MethodName.PHILIPS_PERRON);
        } else {
            core.remove(MethodName.PHILIPS_PERRON);
        }
    }

    public boolean isVecm() {
        return this.core.contains(MethodName.VECM);
    }

    public void setVecm(boolean bool) {
        //core.setAdfLag(bool ? VARModelsSpecification.DEF1 : 0);
        if (bool) {
            core.add(MethodName.VECM);
        } else {
            core.remove(MethodName.VECM);
        }
    }

    public int getVecmOrder() {
        return this.core.getVecmOrder();
    }

    public void setVecmOrder(int value) {
        core.setVecmOrder(value);
    }

    public int getVecmRank() {
        return this.core.getVecmRank();
    }

    public void setVecmRank(int value) {
        core.setVecmRank(value);
    }

    public boolean isBgTest() {
        return this.core.contains(MethodName.BG_TEST);
    }

    public void setBgTest(boolean bool) {
        if (bool) {
            core.add(MethodName.BG_TEST);
        } else {
            core.remove(MethodName.BG_TEST);
        }
    }

    public int getBgOrder() {
        return this.core.getBgOrder();
    }

    public void setBgOrder(int value) {
        core.setBgOrder(value);
    }

    public boolean isLbTest() {
        return this.core.contains(MethodName.LB_TEST);
    }

    public void setLbTest(boolean bool) {
        if (bool) {
            core.add(MethodName.LB_TEST);
        } else {
            core.remove(MethodName.LB_TEST);
        }
    }

    public int getLbOrder() {
        return this.core.getLbOrder();
    }

    public void setLbOrder(int value) {
        core.setLbOrder(value);
    }

    public boolean isCoint() {
        return this.core.contains(MethodName.COINT);
    }

    public void setCoint(boolean bool) {
        if (bool) {
            core.add(MethodName.COINT);
        } else {
            core.remove(MethodName.COINT);
        }
    }

    public int getCointOrder() {
        return this.core.getCointOrder();
    }

    public void setCointOrder(int value) {
        core.setCointOrder(value);
    }

    public CointegrationDetailType getCointDetail() {
        return this.core.getCointDetail();
    }
    
    public void setCointDetail(CointegrationDetailType type)  {
        core.setCointDetail(type);
    }
    
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = methodDesc("df", DFTI_ID, DF_NAME, DF_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("adf", ADF_ID, ADF_NAME, ADF_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("adfLag", ADF_LAG_ID, ADF_LAG_NAME, ADF_LAG_DESC, !core.contains(MethodName.ADF));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("dfti", DFTI_ID, DFTI_NAME, DFTI_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("dftiLag", DFTI_LAG_ID, DFTI_LAG_NAME, DFTI_LAG_DESC, !core.contains(MethodName.DFTI));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("pp", PP_ID, PP_NAME, PP_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("vecm", VECM_ID, VECM_NAME, VECM_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("vecmOrder", VECM_ORDER_ID, VECM_ORDER_NAME, VECM_ORDER_DESC, !core.contains(MethodName.VECM));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("vecmRank", VECM_RANK_ID, VECM_RANK_NAME, VECM_RANK_DESC, !core.contains(MethodName.VECM));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("bgTest", BG_TEST_ID, BG_TEST_NAME, BG_TEST_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("bgOrder", BG_ORDER_ID, BG_ORDER_NAME, BG_ORDER_DESC, !core.contains(MethodName.BG_TEST));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("lbTest", LB_TEST_ID, LB_TEST_NAME, LB_TEST_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("lbOrder", LB_ORDER_ID, LB_ORDER_NAME, LB_ORDER_DESC, !core.contains(MethodName.LB_TEST));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("coint", COINT_ID, COINT_NAME, COINT_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("cointOrder", COINT_ORDER_ID, COINT_ORDER_NAME, COINT_ORDER_DESC, !core.contains(MethodName.COINT));
        if (desc != null) {
            descs.add(desc);
        }
         desc = methodDescRO("cointDetail", COINT_DETAIL_ID, COINT_DETAIL_NAME, COINT_DETAIL_DESC, !core.contains(MethodName.COINT));
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    private EnhancedPropertyDescriptor methodDesc(String propertyName, int pos, String displayName, String shortDescription) {
        try {
            PropertyDescriptor desc = new PropertyDescriptor(propertyName, this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, pos);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(displayName);
            desc.setShortDescription(shortDescription);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor methodDescRO(String propertyName, int pos, String displayName, String shortDescription, boolean readOnly) {
        try {
            PropertyDescriptor desc = new PropertyDescriptor(propertyName, this.getClass(), "get" + StringUtils.capitalize(propertyName), "set" + StringUtils.capitalize(propertyName));
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, pos);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(displayName);
            desc.setShortDescription(shortDescription);
            edesc.setReadOnly(readOnly);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    class ArrayRenderer extends DefaultTableCellRenderer {

        public ArrayRenderer() {
            super();
        }

        @Override
        protected void setValue(Object value) {
            setText("");
        }
    }
}
