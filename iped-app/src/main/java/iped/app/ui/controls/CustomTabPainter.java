package iped.app.ui.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.eclipse.OwnedRectEclipseBorder;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.EclipseTabPane;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.BorderedComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.InvisibleTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.InvisibleTabPane;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.RectGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabComponent;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPanePainter;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.Transparency;
import bibliothek.gui.dock.util.color.AbstractDockColor;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;

public class CustomTabPainter implements TabPainter {
    private static final RenderingHints renderingHints;
    static {
        Map<Key, Object> hints = new HashMap<>();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        renderingHints = new RenderingHints(hints);
    }
    private static final Stroke stroke = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public Border getFullBorder(BorderedComponent owner, DockController controller, Dockable dockable) {
        OwnedRectEclipseBorder border = new OwnedRectEclipseBorder(owner, controller, true) {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g;
                Stroke oldStroke = g2.getStroke();
                RenderingHints oldHints = g2.getRenderingHints();
                g2.setRenderingHints(renderingHints);
                g2.setStroke(stroke);

                Color color = controller.getColors().get("stack.border");
                if (color == null) {
                    color = RexSystemColor.getBorderColor();
                }
                g.setColor(color);
                g.drawRoundRect(1, 1, width - 2, height - 2, 10, 10);

                g2.setRenderingHints(oldHints);
                g2.setStroke(oldStroke);
            }

            public Insets getBorderInsets(Component c) {
                return new Insets(1, 1, 1, 1);
            }

            public boolean isBorderOpaque() {
                return false;
            }
        };
        return border;
    }

    public TabComponent createTabComponent(EclipseTabPane pane, Dockable dockable) {
        RectGradientPainter rgp = new RectGradientPainter(pane, dockable) {
            private static final long serialVersionUID = -9020339124009415001L;

            public void setLabelInsets(Insets labelInsets) {
                super.setLabelInsets(new Insets(3, 1, 4, -5));
            }

            public void setIcon(Icon icon) {
                super.setIcon(null);
            }

            public void paintForeground(Graphics g) {
                if (!isSelected() && !isNextTabSelected()) {
                    Graphics2D g2 = (Graphics2D) g;
                    Stroke oldStroke = g2.getStroke();
                    RenderingHints oldHints = g2.getRenderingHints();
                    g2.setRenderingHints(renderingHints);
                    g2.setStroke(stroke);

                    Color lineColor = colorStackBorder.value();
                    int width = getWidth();
                    int height = getHeight();
                    g.setColor(lineColor);
                    g.drawLine(width - 1, 0, width - 1, height);

                    g2.setRenderingHints(oldHints);
                    g2.setStroke(oldStroke);
                }
            }

            public void paintBackground(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Stroke oldStroke = g2.getStroke();
                RenderingHints oldHints = g2.getRenderingHints();
                g2.setRenderingHints(renderingHints);
                g2.setStroke(stroke);

                int height = getHeight();
                Color color1, color2;
                boolean focusTemporarilyLost = isFocusTemporarilyLost();
                boolean isSelected = isSelected();

                if (!isEnabled()) {
                    color1 = colorStackTabTopDisabled.value();
                    color2 = colorStackTabBottomDisabled.value();
                } else if (isFocused() && !focusTemporarilyLost) {
                    color1 = colorStackTabTopSelectedFocused.value();
                    color2 = colorStackTabBottomSelectedFocused.value();
                } else if (isFocused() && focusTemporarilyLost) {
                    color1 = colorStackTabTopSelectedFocusLost.value();
                    color2 = colorStackTabBottomSelectedFocusLost.value();
                } else if (isSelected) {
                    color1 = colorStackTabTopSelected.value();
                    color2 = colorStackTabBottomSelected.value();
                } else {
                    color1 = colorStackTabTop.value();
                    color2 = colorStackTabBottom.value();
                }

                Paint oldPaint = g2.getPaint();
                g2.setPaint(color1 == color2 ? color1 : new GradientPaint(0, 0, color1, 0, height, color2));

                int x = getTabIndex() == 0 ? 0 : 1;
                int width = getWidth();
                RoundRectangle2D r1 = new RoundRectangle2D.Double(x, 0, width - 1 - x, height + 1, 10, 10);
                Rectangle2D r2 = new Rectangle2D.Double(x, height - 9, width - 1 - x, 10);
                Area a = new Area(r1);
                a.add(new Area(r2));
                if (getTransparency() != Transparency.TRANSPARENT) {
                    g2.fill(a);
                }
                if (isSelected) {
                    Shape oldClip = g2.getClip();
                    Rectangle2D r3 = new Rectangle2D.Double(0, height, width, 1);
                    Area clip = new Area(new Rectangle2D.Double(0, 0, width, height + 1));
                    clip.subtract(new Area(r3));
                    g2.setClip(clip);
                    g2.setColor(colorStackBorder.value());
                    g2.draw(a);
                    g2.setClip(oldClip);
                }
                g2.setPaint(oldPaint);
                g2.setRenderingHints(oldHints);
                g2.setStroke(oldStroke);
            }
        };
        return rgp;
    }

    public InvisibleTab createInvisibleTab(InvisibleTabPane pane, Dockable dockable) {
        return RectGradientPainter.FACTORY.createInvisibleTab(pane, dockable);
    }

    public TabPanePainter createDecorationPainter(EclipseTabPane pane) {
        TabPanePainter tpp = new TabPanePainter() {
            private AbstractDockColor color = new AbstractDockColor("stack.border", DockColor.KIND_DOCK_COLOR,
                    Color.BLACK) {
                protected void changed(Color oldColor, Color newColor) {
                    pane.repaint();
                }
            };

            public void setController(DockController controller) {
                ColorManager colors = controller == null ? null : controller.getColors();
                color.setManager(colors);
            }

            public void paintBackground(Graphics g) {
            }

            public void paintForeground(Graphics g) {
                Dockable selection = pane.getSelectedDockable();
                if (selection == null)
                    return;

                EclipseTab tab = pane.getTab(selection);
                if (tab == null || !tab.isPaneVisible())
                    return;

                Graphics2D g2 = (Graphics2D) g;
                Stroke oldStroke = g2.getStroke();
                RenderingHints oldHints = g2.getRenderingHints();
                g2.setRenderingHints(renderingHints);
                g2.setStroke(stroke);

                Rectangle bounds = tab.getBounds();
                Rectangle available = pane.getAvailableArea();

                g.setColor(color.value());

                paintHorizontal(g, available, bounds, bounds.y + bounds.height);

                g2.setRenderingHints(oldHints);
                g2.setStroke(oldStroke);
            }

            private void paintHorizontal(Graphics g, Rectangle available, Rectangle bounds, int y) {
                if (available.x < bounds.x) {
                    g.drawLine(available.x, y, bounds.x + 1, y);
                }

                if (available.x + available.width > bounds.x + bounds.width) {
                    g.drawLine(available.x + available.width, y, bounds.x + bounds.width - 1, y);
                }
            }
        };
        return tpp;
    }
}
