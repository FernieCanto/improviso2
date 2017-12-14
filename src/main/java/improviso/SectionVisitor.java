/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

/**
 *
 * @author User
 */
public interface SectionVisitor {
    public void visit(FixedSection section);
    public void visit(VariableSection section);
}
