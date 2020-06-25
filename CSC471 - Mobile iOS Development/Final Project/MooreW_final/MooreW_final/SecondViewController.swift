//
//  SecondViewController.swift
//  MooreW_final
//
//  Created by William Moore on 3/16/20.
//  Copyright © 2020 Willy Moore. All rights reserved.
//

import UIKit
import GoogleMaps

class SecondViewController: UIViewController {
    
    //API Key
    

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    //Used from https://developers.google.com/maps/documentation/ios-sdk/map-with-marker
    
    override func loadView() {

        //Create map and the center
        let camera = GMSCameraPosition.camera(withLatitude: 41.953433, longitude: -87.747327, zoom: 12)
        let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
        view = mapView
        
        //Create red pinpoint on map
        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: 41.953433, longitude: -87.747327)
        marker.title = "Chicago"
        marker.snippet = "Illinois"
        marker.map = mapView
        
        //Create circle on map
        let circle = GMSCircle(position: CLLocationCoordinate2D(latitude: 41.953433, longitude: -87.747327), radius: CLLocationDistance(10.0))
        circle.fillColor = UIColor.gray
        circle.map = mapView
    }

    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
