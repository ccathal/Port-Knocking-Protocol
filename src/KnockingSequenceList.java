package src;

import java.util.ArrayList;

// **********currently a redendent class**********
public class KnockingSequenceList extends ArrayList<AttemptKnockingSequence> {
	@Override
    public boolean add(AttemptKnockingSequence e) {
        if(contains(e)){
            return false;
        }
        super.add(e);
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if(!(o instanceof AttemptKnockingSequence))
            return false;

        AttemptKnockingSequence e = (AttemptKnockingSequence) o;

        for(AttemptKnockingSequence item : this) {
            if(item.getPort() == e.getPort() && 
               item.getAddress().equals(e.getAddress())){
                return true;
            }
        }
        return false;
   }

    public AttemptKnockingSequence get(AttemptKnockingSequence e) {
		for(AttemptKnockingSequence item : this) {
            if(item.getPort() == e.getPort() && item.getAddress().equals(e.getAddress())) {
            	return item;
            }
        }
		return e;
	}

}
