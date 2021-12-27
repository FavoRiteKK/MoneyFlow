//
//  CategoriesViewModel.swift
//  iosApp (iOS)
//
//  Created by Marco Gomiero on 30/11/2020.
//

import shared
import Combine

class CategoriesViewModel: ObservableObject {
    
    @Published var categoriesModel: CategoryModel = CategoryModel.Loading()
    
    private var subscriptions = Set<AnyCancellable>()
    
    private var categoriesUseCase: CategoriesUseCaseIos = DI.getCategoriesUseCase()
    
    func startObserving() {
        
        createPublisher(categoriesUseCase.getCategories())
            .eraseToAnyPublisher()
            .receive(on: DispatchQueue.global(qos: .userInitiated))
            .sink(
                receiveCompletion: { completion in
                    if case let .failure(error) = completion {
                        self.categoriesModel = CategoryModel.Error(message: "Something wrong here :(")
                    }
                },
                receiveValue: { genericResponse in
                    onMainThread {
                        self.categoriesModel = genericResponse
                    }
                }
            )
            .store(in: &self.subscriptions)
    }
    
    func stopObserving() {
        self.categoriesUseCase.onDestroy()
    }
}
