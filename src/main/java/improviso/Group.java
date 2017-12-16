package improviso;
import java.util.*;

/**
 * Defines a generic group, implementing the algorithm for adding children
 * groups. A group can be a Node, having multiple children (subgroups) or a
 * Leaf, containing a single Pattern. When a Group is executed, a new subitem
 * is selected
 * 
 * Implementa um grupo genérico, incluindo o algoritmo de adição de groups
 * children. Um grupo pode ser um Nodo, contendo diversos children (sub-itens) ou
 * uma Folha, contendo um Pattern. Quando um grupo é executado, um novo sub-item
 * é selecionado, e o Pattern correspondente pode ser obtido com o método
 * getSelectedPattern. Uma GroupMessage será retornado, contendo sinalizações de
 * fim de execução ou interrupções da seção.
 * RESPONSABILIDADES DO GRUPO:
 *  - Selecionar sub-itens seguindo um algoritmo próprio
 *  - Retornar mensagem com informações de fim
 * @author fernando
 */
public abstract class Group implements java.io.Serializable {
    final private String id;
    final protected ArrayList<Group> children;
    final protected GroupSignal finishedSignal;
    final protected GroupSignal interruptSignal;
    
    final protected Group[] childrenArray; // ?
    
    protected GroupMessage message = null;
    
    protected boolean debug = false;
    protected Pattern selectedPattern = null;
    protected Integer selectedGroupIndex = null;
    protected int executions = 0;
    
    final protected boolean disableTrack = false; // NOT CONFIGURED
    final protected boolean interruptSection = false; // NOT CONFIGURED
    
    public static abstract class GroupBuilder {
        private String id;
        private final ArrayList<Group> children = new ArrayList<>();
        private GroupSignal finishedSignal;
        private GroupSignal interruptSignal;
        
        public String getId() {
            return this.id;
        }
        
        public GroupBuilder setId(String id) {
            this.id = id;
            return this;
        }
        
        public ArrayList<Group> getChildren() {
            return this.children;
        }
        
        public GroupBuilder addChild(Group child) {
            this.children.add(child);
            return this;
        }
        
        public GroupSignal getFinishedSignal() {
            return this.finishedSignal;
        }
        
        public GroupBuilder setFinishedSignal(GroupSignal finishedSignal) {
            this.finishedSignal = finishedSignal;
            return this;
        }
        
        public GroupSignal getInterruptSignal() {
            return this.interruptSignal;
        }
        
        public GroupBuilder setInterruptSignal(GroupSignal interruptSignal) {
            this.interruptSignal = interruptSignal;
            return this;
        }
        
        abstract public Group build();
    }
    
    protected Group(GroupBuilder builder) {
        this.id = builder.getId();
        
        this.childrenArray = new Group[builder.getChildren().size()];
        builder.getChildren().toArray(this.childrenArray);
        this.children = builder.getChildren();
        
        if (builder.getFinishedSignal() != null) {
            this.finishedSignal = builder.getFinishedSignal();
        } else {
            this.finishedSignal = new GroupSignal(100, 100, 1.0);
        }
        
        if (builder.getInterruptSignal() != null) {
            this.interruptSignal = builder.getInterruptSignal();
        } else {
            this.interruptSignal = new GroupSignal(100, 100, 1.0);
        }
    }
    
    public void resetGroup() {
        this.executions = 0;
        children.forEach((g) -> {
            g.resetGroup();
        });
    }
    
    /**
     * Seleciona o próximo sub-item de acordo com o algoritmo e
     * configurações internas do grupo, retornando a mensagem
     * para a trilha.
     * @param random
     * @return Mensagem com opções de fim de execução
     */
    public Pattern.PatternExecution execute(Random random) {
        Pattern.PatternExecution pattern = selectPattern(random);
        message = generateMessage();
        
        executions++;
        if(this.finishedSignal.signal(executions, random)) {
            message.setFinished();
            if(disableTrack) {
                message.setDisable();
            }
        }
        
        if(this.interruptSignal.signal(executions, random)) {
            message.setInterrupt();
            if(interruptSection) {
                message.setInterruptSection();
            }
        }
        
        return pattern;
    }
    
    public GroupMessage getMessage() {
        return message;
    }
    
    public String getId() {
        return this.id;
    }
    
    public int getExecutions() {
        return this.executions;
    }
    
    public ArrayList<Group> getChildren() {
        return this.children;
    }
    
    protected abstract Pattern.PatternExecution selectPattern(Random rand);
    protected abstract GroupMessage generateMessage();
}