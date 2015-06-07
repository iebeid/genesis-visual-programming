
class OpVal extends GenesisVal
{
        protected int val;

        OpVal( int d )
        {
                val = d;
        }

        public String toString()
        {
                return  String.valueOf( val ) ;
        }


        public int getVal()
        {
                return val; 
        }


}


