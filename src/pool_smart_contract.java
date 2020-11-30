import java.util.*;  
public class pool_smart_contract{

    boolean pool_init;
    float m_btc_price_USD;
    float m_apple_price_USD;

    float m_btc_amount_pool;
    float m_apple_amount_pool;

    float m_apple_price_pool;// apple price in btc

    float m_apple_amount_total; // inside and outside the pool - total amount issued 
    float m_order_step = (1/100); // the % of order/m_apple_amount_pool that requires new price calculation
 
    private initiate_pool (float btc_amount_init ){
        if(pool_init == false)
            m_btc_amount_pool = btc_amount_init;
            stablize_apple_amount();
            pool_init = true;
        }
    }   


    private mint ( float new_apple_tokens){
        m_apple_price_pool      += new_apple_tokens;
        m_apple_amount_total    += new_apple_tokens;
    }

     private burn ( float extra_apple_tokens){
        m_apple_price_pool      -= extra_apple_tokens;
        m_apple_amount_total    -= extra_apple_tokens;
    }   

    private update_apple_price_pool(){ // ratio price between btc and apple
        m_apple_price_pool = (m_btc_amount_pool)/m_apple_amount_pool;
    }

        //TODO stabalize according to total apple tokens not only tokens in pool
    private stablize_apple_amount (float btc_price =  -1 , float apple_price =-1){
        
        if( btc_price == -1){ // no given value 
            m_btc_price_USD = 20000;//TODO : update to get from api request
        }else {
            m_btc_price_USD = btc_price;
        }

        if (apple_price == -1){
            m_apple_price_USD = 116;  
        }else {
            m_btc_price_USD = apple_price;
        }

        float  new_apple_amount_pool = ( m_btc_price_USD * m_btc_amount_pool)/m_apple_price_USD;
        if ( new_apple_amount_pool > m_apple_amount_pool){
            mint(new_apple_amount_pool - m_apple_amount_pool);
        } else {
            burn(m_apple_amount_pool - new_apple_amount_pool);
        }
    }

    public info (){
        System.out.println("m_btc_price_USD = %f",m_btc_price_USD );
        System.out.println("m_apple_price_USD= %f",m_apple_price_USD);
        System.out.println("m_btc_amount_pool= %f",m_btc_amount_pool);
        System.out.println("m_apple_amount_pool= %f",m_apple_amount_pool);
        System.out.println("m_apple_price_pool= %f",m_apple_price_pool);
        System.out.println("m_apple_amount_total= %f",m_apple_amount_total);
    }



    public add_btc_to_pool ( float btc_amount){
        m_btc_amount_pool +=btc_amount;
        stablize_apple_amount();
    }

    public sub_btc_from_pool ( float btc_amount){
        m_btc_amount_pool -=btc_amount;
        stablize_apple_amount();
    }    

    public float apple_buy(float apple_amount_to_buy){
        float order_percent_size = (apple_amount_to_buy /m_apple_amount_pool);
        float btc_amount_order_step=0;
        float btc_amount_to_sell =0;
        while (  order_percent_size > m_order_step ){
            update_apple_price_pool();     
            btc_amount_order_step = m_apple_price_pool * m_order_step;
            m_btc_amount_pool  += btc_amount_order_step;
            m_apple_amount_pool-= btc_amount_order_step * m_apple_price_pool;   
            order_percent_size -= m_order_step;
            btc_amount_to_sell += btc_amount_order_step;
        }
        update_apple_price_pool();     
        btc_amount_to_sell += m_apple_price_pool * order_percent_size ;
        return btc_amount_to_sell;
    }

    public boolean apple_sell(float apple_amount_to_sell){
        float order_percent_size = (apple_amount_to_sell / m_apple_amount_pool);
        float btc_amount_order_step=0;
        float btc_amount_to_buy =0;
        while (  order_percent_size > m_order_step ){
            update_apple_price_pool();     
            btc_amount_order_step = m_apple_price_pool * m_order_step;
            m_btc_amount_pool  -= btc_amount_order_step;
            m_apple_amount_pool+= btc_amount_order_step * m_apple_price_pool;   
            order_percent_size -= m_order_step;
            btc_amount_to_buy += btc_amount_order_step;
        }
        update_apple_price_pool();     
        btc_amount_to_buy += m_apple_price_pool * order_percent_size ;
        return btc_amount_to_buy;
    }

    
     public static void main(String []args){
        List<List<String>> apple_records = new ArrayList<>();
        List<List<String>> btc_records = new ArrayList<>(); 
                       
        System.out.println("Hello Stocken");
        Scanner sc=new Scanner(System.in);
        System.out.println("Initiate apple pool simulation with USD amount for Bitcoin store in pool..  ");
        int usd_pool_init = nextInt();
        System.out.println("Choose simulation start date DD/MM/YY ( start after 02/02/12..)  ");
        String str= sc.nextLine(); 


        try (BufferedReader br = new BufferedReader(new FileReader("AAPL Historical Data.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                apple_records.add(Arrays.asList(values));
            }
        } 


        try (BufferedReader br = new BufferedReader(new FileReader("BTC_USD Bitfinex Historical Data.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                btc_records.add(Arrays.asList(values));
            }
        }           

        

     }
     
    
}
