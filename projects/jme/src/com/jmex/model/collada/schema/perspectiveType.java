/**
 * perspectiveType.java This file was generated by XMLSpy 2006sp2 Enterprise
 * Edition. YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE OVERWRITTEN WHEN
 * YOU RE-RUN CODE GENERATION. Refer to the XMLSpy Documentation for further
 * details. http://www.altova.com/xmlspy
 */

package com.jmex.model.collada.schema;

public class perspectiveType extends com.jmex.model.collada.xml.Node {

    private static final long serialVersionUID = 1L;

    public perspectiveType(perspectiveType node) {
        super(node);
    }

    public perspectiveType(org.w3c.dom.Node node) {
        super(node);
    }

    public perspectiveType(org.w3c.dom.Document doc) {
        super(doc);
    }

    public perspectiveType(com.jmex.model.collada.xml.Document doc,
            String namespaceURI, String prefix, String name) {
        super(doc, namespaceURI, prefix, name);
    }

    public void adjustPrefix() {
        for (org.w3c.dom.Node tmpNode = getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "xfov"); tmpNode != null; tmpNode = getDomNextChild(
                Element, "http://www.collada.org/2005/11/COLLADASchema",
                "xfov", tmpNode)) {
            internalAdjustPrefix(tmpNode, true);
            new TargetableFloat(tmpNode).adjustPrefix();
        }
        for (org.w3c.dom.Node tmpNode = getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "yfov"); tmpNode != null; tmpNode = getDomNextChild(
                Element, "http://www.collada.org/2005/11/COLLADASchema",
                "yfov", tmpNode)) {
            internalAdjustPrefix(tmpNode, true);
            new TargetableFloat(tmpNode).adjustPrefix();
        }
        for (org.w3c.dom.Node tmpNode = getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "aspect_ratio"); tmpNode != null; tmpNode = getDomNextChild(
                Element, "http://www.collada.org/2005/11/COLLADASchema",
                "aspect_ratio", tmpNode)) {
            internalAdjustPrefix(tmpNode, true);
            new TargetableFloat(tmpNode).adjustPrefix();
        }
        for (org.w3c.dom.Node tmpNode = getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "znear"); tmpNode != null; tmpNode = getDomNextChild(
                Element, "http://www.collada.org/2005/11/COLLADASchema",
                "znear", tmpNode)) {
            internalAdjustPrefix(tmpNode, true);
            new TargetableFloat(tmpNode).adjustPrefix();
        }
        for (org.w3c.dom.Node tmpNode = getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "zfar"); tmpNode != null; tmpNode = getDomNextChild(
                Element, "http://www.collada.org/2005/11/COLLADASchema",
                "zfar", tmpNode)) {
            internalAdjustPrefix(tmpNode, true);
            new TargetableFloat(tmpNode).adjustPrefix();
        }
    }

    public static int getxfovMinCount() {
        return 1;
    }

    public static int getxfovMaxCount() {
        return 1;
    }

    public int getxfovCount() {
        return getDomChildCount(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "xfov");
    }

    public boolean hasxfov() {
        return hasDomChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "xfov");
    }

    public TargetableFloat newxfov() {
        return new TargetableFloat(domNode.getOwnerDocument().createElementNS(
                "http://www.collada.org/2005/11/COLLADASchema", "xfov"));
    }

    public TargetableFloat getxfovAt(int index) throws Exception {
        return new TargetableFloat(dereference(getDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "xfov", index)));
    }

    public org.w3c.dom.Node getStartingxfovCursor() throws Exception {
        return getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "xfov");
    }

    public org.w3c.dom.Node getAdvancedxfovCursor(org.w3c.dom.Node curNode)
            throws Exception {
        return getDomNextChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "xfov", curNode);
    }

    public TargetableFloat getxfovValueAtCursor(org.w3c.dom.Node curNode)
            throws Exception {
        if (curNode == null)
            throw new com.jmex.model.collada.xml.XmlException("Out of range");
        else
            return new TargetableFloat(dereference(curNode));
    }

    public TargetableFloat getxfov() throws Exception {
        return getxfovAt(0);
    }

    public void removexfovAt(int index) {
        removeDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "xfov", index);
    }

    public void removexfov() {
        while (hasxfov())
            removexfovAt(0);
    }

    public void addxfov(TargetableFloat value) {
        appendDomElement("http://www.collada.org/2005/11/COLLADASchema",
                "xfov", value);
    }

    public void insertxfovAt(TargetableFloat value, int index) {
        insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "xfov", index, value);
    }

    public void replacexfovAt(TargetableFloat value, int index) {
        replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "xfov", index, value);
    }

    public static int getyfovMinCount() {
        return 1;
    }

    public static int getyfovMaxCount() {
        return 1;
    }

    public int getyfovCount() {
        return getDomChildCount(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "yfov");
    }

    public boolean hasyfov() {
        return hasDomChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "yfov");
    }

    public TargetableFloat newyfov() {
        return new TargetableFloat(domNode.getOwnerDocument().createElementNS(
                "http://www.collada.org/2005/11/COLLADASchema", "yfov"));
    }

    public TargetableFloat getyfovAt(int index) throws Exception {
        return new TargetableFloat(dereference(getDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "yfov", index)));
    }

    public org.w3c.dom.Node getStartingyfovCursor() throws Exception {
        return getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "yfov");
    }

    public org.w3c.dom.Node getAdvancedyfovCursor(org.w3c.dom.Node curNode)
            throws Exception {
        return getDomNextChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "yfov", curNode);
    }

    public TargetableFloat getyfovValueAtCursor(org.w3c.dom.Node curNode)
            throws Exception {
        if (curNode == null)
            throw new com.jmex.model.collada.xml.XmlException("Out of range");
        else
            return new TargetableFloat(dereference(curNode));
    }

    public TargetableFloat getyfov() throws Exception {
        return getyfovAt(0);
    }

    public void removeyfovAt(int index) {
        removeDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "yfov", index);
    }

    public void removeyfov() {
        while (hasyfov())
            removeyfovAt(0);
    }

    public void addyfov(TargetableFloat value) {
        appendDomElement("http://www.collada.org/2005/11/COLLADASchema",
                "yfov", value);
    }

    public void insertyfovAt(TargetableFloat value, int index) {
        insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "yfov", index, value);
    }

    public void replaceyfovAt(TargetableFloat value, int index) {
        replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "yfov", index, value);
    }

    public static int getaspect_ratioMinCount() {
        return 1;
    }

    public static int getaspect_ratioMaxCount() {
        return 1;
    }

    public int getaspect_ratioCount() {
        return getDomChildCount(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "aspect_ratio");
    }

    public boolean hasaspect_ratio() {
        return hasDomChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "aspect_ratio");
    }

    public TargetableFloat newaspect_ratio() {
        return new TargetableFloat(domNode.getOwnerDocument().createElementNS(
                "http://www.collada.org/2005/11/COLLADASchema", "aspect_ratio"));
    }

    public TargetableFloat getaspect_ratioAt(int index) throws Exception {
        return new TargetableFloat(dereference(getDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "aspect_ratio",
                index)));
    }

    public org.w3c.dom.Node getStartingaspect_ratioCursor() throws Exception {
        return getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "aspect_ratio");
    }

    public org.w3c.dom.Node getAdvancedaspect_ratioCursor(
            org.w3c.dom.Node curNode) throws Exception {
        return getDomNextChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "aspect_ratio",
                curNode);
    }

    public TargetableFloat getaspect_ratioValueAtCursor(org.w3c.dom.Node curNode)
            throws Exception {
        if (curNode == null)
            throw new com.jmex.model.collada.xml.XmlException("Out of range");
        else
            return new TargetableFloat(dereference(curNode));
    }

    public TargetableFloat getaspect_ratio() throws Exception {
        return getaspect_ratioAt(0);
    }

    public void removeaspect_ratioAt(int index) {
        removeDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "aspect_ratio",
                index);
    }

    public void removeaspect_ratio() {
        while (hasaspect_ratio())
            removeaspect_ratioAt(0);
    }

    public void addaspect_ratio(TargetableFloat value) {
        appendDomElement("http://www.collada.org/2005/11/COLLADASchema",
                "aspect_ratio", value);
    }

    public void insertaspect_ratioAt(TargetableFloat value, int index) {
        insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "aspect_ratio", index, value);
    }

    public void replaceaspect_ratioAt(TargetableFloat value, int index) {
        replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "aspect_ratio", index, value);
    }

    public static int getznearMinCount() {
        return 1;
    }

    public static int getznearMaxCount() {
        return 1;
    }

    public int getznearCount() {
        return getDomChildCount(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "znear");
    }

    public boolean hasznear() {
        return hasDomChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "znear");
    }

    public TargetableFloat newznear() {
        return new TargetableFloat(domNode.getOwnerDocument().createElementNS(
                "http://www.collada.org/2005/11/COLLADASchema", "znear"));
    }

    public TargetableFloat getznearAt(int index) throws Exception {
        return new TargetableFloat(
                dereference(getDomChildAt(Element,
                        "http://www.collada.org/2005/11/COLLADASchema",
                        "znear", index)));
    }

    public org.w3c.dom.Node getStartingznearCursor() throws Exception {
        return getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "znear");
    }

    public org.w3c.dom.Node getAdvancedznearCursor(org.w3c.dom.Node curNode)
            throws Exception {
        return getDomNextChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "znear",
                curNode);
    }

    public TargetableFloat getznearValueAtCursor(org.w3c.dom.Node curNode)
            throws Exception {
        if (curNode == null)
            throw new com.jmex.model.collada.xml.XmlException("Out of range");
        else
            return new TargetableFloat(dereference(curNode));
    }

    public TargetableFloat getznear() throws Exception {
        return getznearAt(0);
    }

    public void removeznearAt(int index) {
        removeDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "znear", index);
    }

    public void removeznear() {
        while (hasznear())
            removeznearAt(0);
    }

    public void addznear(TargetableFloat value) {
        appendDomElement("http://www.collada.org/2005/11/COLLADASchema",
                "znear", value);
    }

    public void insertznearAt(TargetableFloat value, int index) {
        insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "znear", index, value);
    }

    public void replaceznearAt(TargetableFloat value, int index) {
        replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "znear", index, value);
    }

    public static int getzfarMinCount() {
        return 1;
    }

    public static int getzfarMaxCount() {
        return 1;
    }

    public int getzfarCount() {
        return getDomChildCount(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "zfar");
    }

    public boolean haszfar() {
        return hasDomChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "zfar");
    }

    public TargetableFloat newzfar() {
        return new TargetableFloat(domNode.getOwnerDocument().createElementNS(
                "http://www.collada.org/2005/11/COLLADASchema", "zfar"));
    }

    public TargetableFloat getzfarAt(int index) throws Exception {
        return new TargetableFloat(dereference(getDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "zfar", index)));
    }

    public org.w3c.dom.Node getStartingzfarCursor() throws Exception {
        return getDomFirstChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "zfar");
    }

    public org.w3c.dom.Node getAdvancedzfarCursor(org.w3c.dom.Node curNode)
            throws Exception {
        return getDomNextChild(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "zfar", curNode);
    }

    public TargetableFloat getzfarValueAtCursor(org.w3c.dom.Node curNode)
            throws Exception {
        if (curNode == null)
            throw new com.jmex.model.collada.xml.XmlException("Out of range");
        else
            return new TargetableFloat(dereference(curNode));
    }

    public TargetableFloat getzfar() throws Exception {
        return getzfarAt(0);
    }

    public void removezfarAt(int index) {
        removeDomChildAt(Element,
                "http://www.collada.org/2005/11/COLLADASchema", "zfar", index);
    }

    public void removezfar() {
        while (haszfar())
            removezfarAt(0);
    }

    public void addzfar(TargetableFloat value) {
        appendDomElement("http://www.collada.org/2005/11/COLLADASchema",
                "zfar", value);
    }

    public void insertzfarAt(TargetableFloat value, int index) {
        insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "zfar", index, value);
    }

    public void replacezfarAt(TargetableFloat value, int index) {
        replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema",
                "zfar", index, value);
    }

    private org.w3c.dom.Node dereference(org.w3c.dom.Node node) {
        return node;
    }
}
