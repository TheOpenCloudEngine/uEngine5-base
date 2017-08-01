package org.uengine.social;

import org.uengine.persistence.rolemapping.RoleMappingDAO;
import org.uengine.util.dao.AbstractGenericDAO;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.RemoveException;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by uengine on 2017. 8. 1..
 */
@Entity
@Table(name = "BPM_ROLEMAPPING")
public class RoleMapping implements RoleMappingDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long roleMappingId;

    @ManyToOne(targetEntity = ProcessInstance.class)
    Long instId;

    @ManyToOne(targetEntity = ProcessInstance.class)
    Long rootInstId;

    String roleName;

    String value;

    String endpoint;

    String resName;

    String groupId;

    Number assignType;

    String assignParam1;

    String dispatchParam1;

    Number dispatchOption;

    @Override
    public Long getRoleMappingId() {
        return roleMappingId;
    }

    @Override
    public void setRoleMappingId(Long roleMappingId) {
        this.roleMappingId = roleMappingId;
    }

    @Override
    public Long getInstId() {
        return instId;
    }

    @Override
    public void setInstId(Long instId) {
        this.instId = instId;
    }

    @Override
    public Long getRootInstId() {
        return rootInstId;
    }

    @Override
    public void setRootInstId(Long rootInstId) {
        this.rootInstId = rootInstId;
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getResName() {
        return resName;
    }

    @Override
    public void setResName(String resName) {
        this.resName = resName;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public Number getAssignType() {
        return assignType;
    }

    @Override
    public void setAssignType(Number assignType) {
        this.assignType = assignType;
    }

    @Override
    public String getAssignParam1() {
        return assignParam1;
    }

    @Override
    public void setAssignParam1(String assignParam1) {
        this.assignParam1 = assignParam1;
    }

    @Override
    public String getDispatchParam1() {
        return dispatchParam1;
    }

    @Override
    public void setDispatchParam1(String dispatchParam1) {
        this.dispatchParam1 = dispatchParam1;
    }

    @Override
    public Number getDispatchOption() {
        return dispatchOption;
    }

    public void setDispatchOption(Number dispatchOption) {
        this.dispatchOption = dispatchOption;
    }


    ///////////////////////////////////////////// dummy implementations ////////////////////////////////////////////////

    @Override
    public EJBLocalHome getEJBLocalHome() throws EJBException {
        return null;
    }

    @Override
    public Object getPrimaryKey() throws EJBException {
        return null;
    }

    @Override
    public void remove() throws RemoveException, EJBException {

    }

    @Override
    public boolean isIdentical(EJBLocalObject ejbLocalObject) throws EJBException {
        return false;
    }

    @Override
    public void select() throws Exception {

    }

    @Override
    public int insert() throws Exception {
        return 0;
    }

    @Override
    public int update() throws Exception {
        return 0;
    }

    @Override
    public int update(String s) throws Exception {
        return 0;
    }

    @Override
    public int call() throws Exception {
        return 0;
    }

    @Override
    public void addBatch() throws Exception {

    }

    @Override
    public int[] updateBatch() throws Exception {
        return new int[0];
    }

    @Override
    public void beforeFirst() throws Exception {

    }

    @Override
    public boolean previous() throws Exception {
        return false;
    }

    @Override
    public boolean next() throws Exception {
        return false;
    }

    @Override
    public boolean first() throws Exception {
        return false;
    }

    @Override
    public void afterLast() throws Exception {

    }

    @Override
    public boolean last() throws Exception {
        return false;
    }

    @Override
    public boolean absolute(int i) throws Exception {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Object get(String s) throws Exception {
        return null;
    }

    @Override
    public Object set(String s, Object o) throws Exception {
        return null;
    }

    @Override
    public String getString(String s) throws Exception {
        return null;
    }

    @Override
    public Integer getInt(String s) throws Exception {
        return null;
    }

    @Override
    public Long getLong(String s) throws Exception {
        return null;
    }

    @Override
    public Boolean getBoolean(String s) throws Exception {
        return null;
    }

    @Override
    public Date getDate(String s) throws Exception {
        return null;
    }

    @Override
    public AbstractGenericDAO getImplementationObject() {
        return null;
    }

    @Override
    public void releaseResource() throws Exception {

    }
}
